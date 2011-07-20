package org.jcoderz.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.xerces.util.XMLCatalogResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XsltBase {
	private static Logger logger = Logger.getLogger(XsltBase.class.getName());

	/** System property for the XML Parser Configuration (Xalan2). */
	private static final String XML_PARSER_CONFIGURATION_PROPERTY = "org.apache.xerces.xni.parser.XMLParserConfiguration";

	/** Xalan2 XML Parser Configuration w/ XInclude support. */
	private static final String XML_PARSER_CONFIG_WITH_XINCLUDE = "org.apache.xerces.parsers.XIncludeParserConfiguration";

	public static void transform(File inFile, String xslFile, File destDir, File outFile, boolean resolveExternalEntities) {
		StreamResult out = null;
		try {
			final String xmlParserConfig = System
					.getProperty(XML_PARSER_CONFIGURATION_PROPERTY);
			if (!XML_PARSER_CONFIG_WITH_XINCLUDE.equals(xmlParserConfig)) {
				System.setProperty(XML_PARSER_CONFIGURATION_PROPERTY,
						XML_PARSER_CONFIG_WITH_XINCLUDE);
			}
			// Xalan2 transformer is required,
			// that why we explicit use this factory
			final TransformerFactory factory = (TransformerFactory) Class.forName("org.apache.xalan.processor.TransformerFactoryImpl")
					.newInstance();

			factory.setURIResolver(new JarArchiveUriResolver());
			final StreamSource source = getXslFileAsSource(xslFile);
			final Transformer transformer = factory.newTransformer(source);
			transformer.setParameter("outdir",
					destDir != null ? destDir.getAbsolutePath() : "");
			final Source xml = getInAsStreamSource("src/xml/catalog.xml", inFile, resolveExternalEntities);

			out = new StreamResult(outFile);
	        // set the stream directly to avoid issues with blanks in the filename.
	        out.setOutputStream(new FileOutputStream(outFile));
			transformer.setErrorListener(new MyErrorListener());
			transformer.transform(xml, out);
		} catch (Exception e) {
			throw new BuildException("Error during transformation: " + e, e);
		} finally {
			if (out != null) {
			         try
			         {
			            out.getOutputStream().close();
			         }
			         catch (IOException x)
			         {
//			             logger.log(Level.FINE, "Error while closing " + OutputStream.class.getName() + ": "
//			                     + out.getOutputStream().getClass().getName() + ".close()", x);
			         }
			}
		}

	}

	private static StreamSource getXslFileAsSource(String xsl) {
		final StreamSource result;
		final InputStream xslStream = XsltBase.class
				.getResourceAsStream(xsl);
		if (xslStream == null) {
			try {
				final File file = new File(xsl);
				final InputStream xslFile = new FileInputStream(file);
				result = new StreamSource(xslFile);
				result.setSystemId(file.toURI().toASCIIString());
			} catch (FileNotFoundException e) {
				throw new BuildException("Cannot locate stylesheet " + xsl, e);
			}
		} else {
			result = new StreamSource(xslStream);
			final URL url = XsltBase.class.getResource(xsl);
			if (url != null) {
				try {
					result.setSystemId(url.toURI().toASCIIString());
				} catch (URISyntaxException ex) {
					// log("Failed to set systemId. Got " + ex,
					// Project.MSG_VERBOSE);
				}
			}
		}
		return result;
	}

	/**
	 * @return a resource stream from in file.
	 * @throws FileNotFoundException
	 */
	private static Source getInAsStreamSource(String catalog, File inFile, boolean resolveExternalEntities) {
		final Source result;
		if (!resolveExternalEntities) {
			final org.xml.sax.XMLReader reader;
			try {
				EntityResolver resolver = getEntityResolver(catalog);

				// reader = XMLReaderFactory.createXMLReader(
				// "org.apache.xerces.parsers.SAXParser");
				reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
				reader.setEntityResolver(resolver);
				result = new SAXSource(reader, new InputSource(
						new FileInputStream(inFile)));
			} catch (SAXException e) {
				throw new BuildException("Cannot create SAX XML Reader: " + e,
						e);
			} catch (FileNotFoundException e) {
				throw new BuildException("Ups, cannot open file: " + e, e);
			}
		} else {
			result = new StreamSource(inFile);
		}
		return result;
	}

	private static class MyErrorListener implements ErrorListener {
		/** {@inheritDoc} */
		public void warning(TransformerException arg0)
				throws TransformerException {
			throw arg0;
		}

		/** {@inheritDoc} */
		public void error(TransformerException arg0)
				throws TransformerException {
			throw arg0;
		}

		/** {@inheritDoc} */
		public void fatalError(TransformerException arg0)
				throws TransformerException {
			throw arg0;
		}
	}

	/**
	 * Instantiates xml resolver for xerces xml parser.
	 * 
	 * If xml-resolver.jar is available on the boot classpath of ant, the
	 * implementation of an xml catalog resolver will be returned otherwise the
	 * dummy resolver implementation will be provided
	 * 
	 * @return EntityResolver entity resolver
	 */
	private static EntityResolver getEntityResolver(String catalog) {
		EntityResolver resolver = new DummyEntityResolver();
		try {
			String[] catalogs = { catalog };
			System.getProperties().put("xml.catalog.verbosity", "1000");

			// Create catalog resolver and set a catalog list.
			XMLCatalogResolver xmlResolver = new XMLCatalogResolver();

			xmlResolver.setPreferPublic(false);
			xmlResolver.setCatalogList(catalogs);
			resolver = xmlResolver;
		} catch (NoClassDefFoundError e) {
			// The most secure way to check for non-existence of the
			// CatalogReader
			// class is within ant class loaders is to catch the
			// NoClassDefFoundError.
			logger.warning("Class CatalogReader (xml-resolver.jar) could not be found "
					+ " within bootstrap classpath. No entity resolver is "
					+ " available, setting dummy resolver.");
		}

		return resolver;
	}

}
