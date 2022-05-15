package com.andrewjoel.cms.provider;

import com.andrewjoel.cms.exceptions.CmsException;
import com.andrewjoel.cms.models.hbm.HbmEntity;
import com.andrewjoel.cms.models.hbm.HbmProperty;
import com.andrewjoel.cms.models.hbm.HbmPropertyType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.andrewjoel.cms.utils.CmsConstants.NAME;
import static com.andrewjoel.cms.utils.CmsConstants.TYPE;

@Component
public final class CmsEntityTemplate {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger(CmsEntityTemplate.class);

    @Value("${spring.jpa.mapping-resources}")
    private String mappingPath;

    public HbmEntity convertXmlToPojo(final String modelName) {
        if (StringUtils.isEmpty(modelName)) {
            throw new CmsException("Model name is empty");
        }

        try {
            final DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            final String modelHbmPath = getHbmModelPath(modelName);

            if (StringUtils.isEmpty(modelHbmPath)) {
                LOGGER.debug("Model file does not exist in the path: {}", modelHbmPath);
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

    private String getHbmModelPath(final String modelName) {
        final String path = mappingPath + modelName + ".hbm.xml";

        if (Files.notExists(Paths.get(path))) {
            return StringUtils.EMPTY;
        }

        return path;
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
        property.setName(node.getAttributes().getNamedItem(NAME).getTextContent());
        property.setType(HbmPropertyType.fromString(node.getAttributes().getNamedItem(TYPE).getTextContent()));
        property.setNullable(node.getAttributes().getNamedItem("not-null").getTextContent()
                .equals(Boolean.TRUE.toString()));
        return property;
    }

    public void convertPojoToXml(final HbmEntity entity) {
        try {
            final Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();
            final Element root = document.createElement("hibernate-mapping");
            final Element classNode = document.createElement("class");

            classNode.setAttribute("entity-name", entity.getClassName());
            classNode.setAttribute("table", entity.getModelName());

        } catch (final ParserConfigurationException parserConfigurationException) {
            LOGGER.error("Parser configuration exception: {}", ExceptionUtils.getStackTrace(parserConfigurationException));
        }
    }
}
