/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json.util;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.User;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.UdrSpecification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility functions to convert JSON objects to JPA domain objects.
 */
public class JsonToDomainObjectConverter {
    /**
     * Creates a meta data domain object from the JSON UDR specification object.
     * 
     * @param inputJson
     *            the UDR JSON object.
     * @return RuleMeta domain object
     */

    private static RuleMeta extractRuleMeta(final UdrSpecification inputJson,
            final boolean checkStartDate) {
        final MetaData metaData = inputJson.getSummary();

        JsonValidationUtils.validateMetaData(metaData, checkStartDate);

        final Long id = inputJson.getId();
        final String title = metaData.getTitle();
        final Date startDate = metaData.getStartDate();
        final String descr = metaData.getDescription();
        final YesNoEnum enabled = metaData.isEnabled() ? YesNoEnum.Y
                : YesNoEnum.N;
        final Date endDate = metaData.getEndDate();

        RuleMeta ret = createRuleMeta(id, title, descr, startDate, endDate,
                enabled);
        return ret;
    }

    public static RuleMeta extractRuleMeta(final UdrSpecification inputJson) {
        return extractRuleMeta(inputJson, true);
    }

    public static RuleMeta extractRuleMetaUpdates(UdrSpecification inputJson) {
        return extractRuleMeta(inputJson, false);
    }

    /**
     * Converts the rule details portion of the UDR JSON specifications object
     * (i.e., QueryObject) object to a blob.
     * 
     * @param qObj
     *            the QueryObject part of JSON UDR object.
     * @return the binary BLOB data corresponding to the QueryObject.
     * @throws IOException
     *             on error
     * @throws ClassNotFoundException
     *             on error
     */
    public static byte[] convertQueryObjectToBlob(QueryObject qObj)
            throws IOException, ClassNotFoundException {
        if (qObj != null) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
            final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
            out.writeObject(qObj);
            out.close();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } else {
            return null;
        }
    }

    /**
     * Extracts the JSON UdrSpecification object from the UdrRule domain object
     * fetched from the DB.
     * 
     * @param rule
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static UdrSpecification getJsonFromUdrRule(UdrRule rule)
            throws IOException, ClassNotFoundException {
        QueryObject qObj = null;
        MetaData meta = createMetadataJson(rule.getAuthor().getUserId(), rule.getMetaData());
        if (rule.getUdrConditionObject() != null) {
            final ByteArrayInputStream bis = new ByteArrayInputStream(
                    rule.getUdrConditionObject());
            final GZIPInputStream gzipInStream = new GZIPInputStream(bis);
            final ObjectInputStream in = new ObjectInputStream(gzipInStream);
            qObj = (QueryObject) in.readObject();
            in.close();
        }
        // create the UDR spec object
        UdrSpecification ret = new UdrSpecification(rule.getId(), qObj, meta);

        return ret;
    }

    /**
     * Creates a JSON meta data object from the meta data information in a
     * domain UDR rule object.
     * 
     * @param uRule
     *            the domain UDR rule object.
     * @return JSON meta data object (i.e., the summary item)
     */
    private static MetaData createMetadataJson(String authorUserId, RuleMeta ruleMeta) {
        
        final MetaData ret = new MetaData(ruleMeta.getTitle(),
                ruleMeta.getDescription(), ruleMeta.getStartDt(), authorUserId);

        ret.setEnabled(ruleMeta.getEnabled() == YesNoEnum.Y ? true : false);
        ret.setEndDate(ruleMeta.getEndDt());

        return ret;
    }

    /**
     * Creates a domain UdrRule object from the JSON UdrSpecification object.
     * (Note: this object can be inserted/updated into the DB.)
     * 
     * @param inputJson
     * @return
     * @throws IOException
     */
    public static UdrRule createUdrRuleFromJson(UdrSpecification inputJson,
            User author) throws IOException {
        if (inputJson == null) {
            throw ErrorHandlerFactory.getErrorHandler().createException(
                    CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, "inputJson",
                    "JsonToDomainObjectConverter.createUdrRuleFromJson()");
        }

        final RuleMeta metaData = extractRuleMeta(inputJson);

        final UdrRule rule = createUdrRule(inputJson.getId(), metaData,
                createQueryObjectBlob(inputJson), author);
        //createEngineRules(rule, inputJson);

        return rule;

    }



    /**
     * Converts a "detail" portion of the UDR JSON object to compressed binary
     * data for saving in the database as a BLOB.
     * 
     * @param json
     *            the JSON object to serialize.
     * @return the binary blob object
     */
    private static byte[] createQueryObjectBlob(UdrSpecification json)
            throws IOException {
        QueryObject qObj = json.getDetails();
        byte[] retBlob = null;
        if (qObj != null) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
            final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
            out.writeObject(qObj);
            out.close();
            retBlob = bos.toByteArray();
        }
        return retBlob;
    }

    /**
     * Creates a UdrRule domain object.
     * 
     * @param id
     *            the Id of the domain UDR Rule object.
     * @param title
     *            the title of the rule.
     * @param descr
     *            the rule description.
     * @param enabled
     *            enabled state of the rule.
     * @return the UDR rule domain object.
     */
    private static UdrRule createUdrRule(Long id, RuleMeta meta,
            byte[] queryObjectBlob, User author) {
        UdrRule rule = new UdrRule();
        rule.setId(id);
        rule.setDeleted(YesNoEnum.N);
        rule.setEditDt(new Date());
        rule.setAuthor(author);
        rule.setTitle(meta.getTitle());
        rule.setMetaData(meta);
        rule.setUdrConditionObject(queryObjectBlob);
        return rule;
    }

    /**
     * Creates the meta data portion of the UdrRule domain object.
     * 
     * @param id
     *            the Id of the domain UDR Rule object.
     * @param title
     *            the title of the rule.
     * @param descr
     *            the rule description.
     * @param startDate
     *            the day the rule should become active (if enabled).
     * @param endDate
     *            the day the rule should cease to be active (optional).
     * @param enabled
     *            enabled state of the rule.
     * @return
     */
    private static RuleMeta createRuleMeta(Long id, String title, String descr,
            Date startDate, Date endDate, YesNoEnum enabled) {
        RuleMeta meta = new RuleMeta();
        if (id != null) {
            meta.setId(id);
        }
        meta.setDescription(descr);
        meta.setEnabled(enabled);
        meta.setHitSharing(YesNoEnum.N);
        meta.setPriorityHigh(YesNoEnum.N);
        meta.setStartDt(startDate);
        meta.setEndDt(endDate);
        meta.setTitle(title);
        return meta;
    }

}
