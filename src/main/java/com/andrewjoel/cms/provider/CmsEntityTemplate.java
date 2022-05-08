package com.andrewjoel.cms.provider;

import com.andrewjoel.cms.models.hbm.HbmEntity;
import com.andrewjoel.cms.models.hbm.HbmProperty;
import com.andrewjoel.cms.models.hbm.HbmPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public final class CmsEntityTemplate {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(CmsEntityTemplate.class);

    public HbmEntity convertXmlToPojo(final String modelName) {
        // TODO: Handle empty modelName

        try {
            final DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            final String modelHbmPath = getClasspathHbmFile(modelName);

            if (StringUtils.isEmpty(modelHbmPath)) {
                return null;
            }

            final Document document = documentBuilder.parse(modelHbmPath);
            final NodeList classNode = document.getElementsByTagName("class");
            final HbmEntity hbmEntity = new HbmEntity();

            hbmEntity.setModelName(modelName);
            hbmEntity.setClassName(classNode.item(0).getAttributes().getNamedItem("entity-name").getTextContent());

            final int length = classNode.item(0).getChildNodes().getLength();

            final Map<String, HbmProperty> attributes = IntStream.rangeClosed(0, length)
                    .mapToObj(index -> classNode.item(0).getChildNodes().item(index))
                    .filter(Objects::nonNull)
                    .filter(x -> x.getNodeType() == Node.ELEMENT_NODE)
                    .map(this::prepareHbmProperty)
                    .collect(Collectors.toMap(HbmProperty::getName, Function.identity()));

            hbmEntity.setAttributes(attributes);
            hbmEntity.setPrimaryKey(findPrimaryKey(classNode));

            return hbmEntity;
        } catch (final ParserConfigurationException parserConfigurationException) {
            LOGGER.error("ParserConfigurationException: {}", ExceptionUtils.getStackTrace(parserConfigurationException));
        } catch (final SAXException saxException) {
            LOGGER.error("SAXException: {}", ExceptionUtils.getStackTrace(saxException));
        } catch (final IOException ioException) {
            LOGGER.error("IOException: {}", ExceptionUtils.getStackTrace(ioException));
        }

        return null;
    }

    private String getClasspathHbmFile(final String modelName) {
        assert StringUtils.isNotEmpty(modelName);

        try {
            return Path.of(ClassLoader.getSystemResource("entities/" + modelName + ".hbm.xml")
                            .toURI())
                    .toString();
        } catch (final URISyntaxException uriSyntaxException) {
            LOGGER.error("Cannot load hbm file: {} due to exception: {}", modelName, ExceptionUtils.getStackTrace(uriSyntaxException));
        }

        return StringUtils.EMPTY;
    }

    private String findPrimaryKey(final NodeList nodeList) {
        return IntStream.range(0, nodeList.item(0).getChildNodes().getLength())
                .mapToObj(index -> nodeList.item(0).getChildNodes().item(index))
                .filter(node -> node.getNodeName().equals("id"))
                .map(node -> node.getAttributes().getNamedItem("name").getTextContent())
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }

    private HbmProperty prepareHbmProperty(final Node node) {
        final HbmProperty property = new HbmProperty();
        property.setName(node.getAttributes().getNamedItem("name").getTextContent());
        property.setType(HbmPropertyType.fromString(node.getAttributes().getNamedItem("type").getTextContent()));
        return property;
    }
}
