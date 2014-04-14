package org.activityinfo.server.endpoint.rest;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.command.DispatcherSync;
import org.codehaus.jackson.JsonGenerator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SitesResources {

    private final DispatcherSync dispatcher;

    public SitesResources(DispatcherSync dispatcher) {
        this.dispatcher = dispatcher;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String query(
            @QueryParam("activity") List<Integer> activityIds,
            @QueryParam("database") List<Integer> databaseIds,
            @QueryParam("indicator") List<Integer> indicatorIds,
            @QueryParam("partner") List<Integer> partnerIds,
            @QueryParam("attribute") List<Integer> attributeIds,
            @QueryParam("location") List<Integer> locationIds,
            @QueryParam("format") String format
    ) throws IOException {

        Filter filter = new Filter();
        filter.addRestriction(DimensionType.Activity, activityIds);
        filter.addRestriction(DimensionType.Database, databaseIds);
        filter.addRestriction(DimensionType.Indicator, indicatorIds);
        filter.addRestriction(DimensionType.Partner, partnerIds);
        filter.addRestriction(DimensionType.Attribute, attributeIds);
        filter.addRestriction(DimensionType.Location, locationIds);

        List<SiteDTO> sites = dispatcher.execute(new GetSites(filter)).getData();

        StringWriter writer = new StringWriter();
        JsonGenerator json = Jackson.createJsonFactory(writer);

        writeJson(sites, json);

        return writer.toString();
    }

    @GET
    @Path("/points")
    public Response queryPoints(
            @QueryParam("activity") List<Integer> activityIds,
            @QueryParam("database") List<Integer> databaseIds,
            @QueryParam("callback") String callback) throws IOException {

        Filter filter = new Filter();
        filter.addRestriction(DimensionType.Activity, activityIds);
        filter.addRestriction(DimensionType.Database, databaseIds);

        List<SiteDTO> sites = dispatcher.execute(new GetSites(filter)).getData();

        StringWriter writer = new StringWriter();
        JsonGenerator json = Jackson.createJsonFactory(writer);
        writeGeoJson(sites, json);

        if (Strings.isNullOrEmpty(callback)) {
            return Response
                    .ok(writer.toString())
                    .type("application/json; charset=UTF-8").build();
        } else {
            return Response
                    .ok(callback + "(" + writer.toString() + ");")
                    .type("application/javascript; charset=UTF-8")
                    .build();
        }
    }


    private void writeJson(List<SiteDTO> sites, JsonGenerator json)
            throws IOException {
        json.writeStartArray();

        for (SiteDTO site : sites) {
            json.writeStartObject();
            json.writeNumberField("id", site.getId());
            json.writeNumberField("activity", site.getActivityId());
            json.writeNumberField("timestamp", site.getTimeEdited());

            // write start / end date if applicable
            if (site.getDate1() != null && site.getDate2() != null) {
                json.writeStringField("startDate", site.getDate1().toString());
                json.writeStringField("endDate", site.getDate2().toString());
            }

            // write the location as a separate object
            json.writeObjectFieldStart("location");
            json.writeNumberField("id", site.getLocationId());
            json.writeStringField("name", site.getLocationName());

            if (site.hasLatLong()) {
                json.writeFieldName("latitude");
                json.writeNumber(site.getLatitude());
                json.writeFieldName("longitude");
                json.writeNumber(site.getLongitude());
            }
            json.writeEndObject();

            json.writeObjectFieldStart("partner");
            json.writeNumberField("id", site.getPartnerId());
            json.writeStringField("name", site.getPartnerName());
            json.writeEndObject();

            if (site.getProject() != null) {
                json.writeNumberField("projectId", site.getProject().getId());
            }

            // write attributes as a series of ids
            Set<Integer> attributes = getAttributeIds(site);
            if (!attributes.isEmpty()) {
                json.writeFieldName("attributes");
                json.writeStartArray();
                for (Integer attributeId : attributes) {
                    json.writeNumber(attributeId);
                }
                json.writeEndArray();
            }

            // write indicators
            Set<Integer> indicatorIds = getIndicatorIds(site);
            if (!indicatorIds.isEmpty()) {
                json.writeObjectFieldStart("indicatorValues");
                for (Integer indicatorId : indicatorIds) {
                    json.writeNumberField(Integer.toString(indicatorId), site.getIndicatorValue(indicatorId));
                }
                json.writeEndObject();
            }

            // comments
            if (!Strings.isNullOrEmpty(site.getComments())) {
                json.writeFieldName("comments");
                json.writeString(site.getComments());
            }

            json.writeEndObject();
        }
        json.writeEndArray();
        json.close();
    }

    private void writeGeoJson(List<SiteDTO> sites, JsonGenerator json) throws IOException {

        json.writeStartObject();
        json.writeStringField("type", "FeatureCollection");
        json.writeArrayFieldStart("features");

        final SchemaDTO schemaDTO = dispatcher.execute(new GetSchema());

        for (SiteDTO site : sites) {
            if (site.hasLatLong()) {
                json.writeStartObject();
                json.writeStringField("type", "Feature");
                json.writeNumberField("id", site.getId());

                // write out the properties object
                json.writeObjectFieldStart("properties");
                json.writeStringField("locationName", site.getLocationName());
                json.writeStringField("partnerName", site.getPartnerName());
                if (!Strings.isNullOrEmpty(site.getComments())) {
                    json.writeStringField("comments", site.getComments());
                }

                final Map<String, Object> indicatorsMap = Maps.newHashMap();
                final Map<String, Object> attributesMap = Maps.newHashMap();

                // write indicators and attributes
                for(String propertyName : site.getPropertyNames()) {
                    if(propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                        Object value = site.get(propertyName);
                        if(value instanceof Number) {
                            final int indicatorId = IndicatorDTO.indicatorIdForPropertyName(propertyName);
                            final IndicatorDTO dto = schemaDTO.getIndicatorById(indicatorId);
                            final double doubleValue = ((Number) value).doubleValue();
                            indicatorsMap.put(dto.getName(), doubleValue);
                        }
                    } else if(propertyName.startsWith(AttributeDTO.PROPERTY_PREFIX)) {
                        Object value = site.get(propertyName);
                        final int attributeId = AttributeDTO.idForPropertyName(propertyName);
                        final AttributeDTO attributeDTO = schemaDTO.getAttributeById(attributeId);
                        attributesMap.put(attributeDTO.getName(), value == Boolean.TRUE);
                    }
                }

                // write indicators inside properties
                Jackson.writeMap(json, "indicators", indicatorsMap);

                // write attributes inside properties
                Jackson.writeMap(json, "attributes", attributesMap);

                json.writeEndObject();

                // write out the geometry object
                json.writeObjectFieldStart("geometry");
                json.writeStringField("type", "Point");
                json.writeArrayFieldStart("coordinates");
                json.writeNumber(site.getX());
                json.writeNumber(site.getY());
                json.writeEndArray();
                json.writeEndObject();

                json.writeEndObject();
            }
        }
        json.writeEndArray();
        json.writeEndObject();
        json.close();
    }

    private Set<Integer> getIndicatorIds(SiteDTO site) {
        Set<Integer> ids = Sets.newHashSet();
        for (String propertyName : site.getPropertyNames()) {
            if (propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX) &&
                    site.get(propertyName) != null) {
                ids.add(IndicatorDTO.indicatorIdForPropertyName(propertyName));
            }
        }
        return ids;
    }

    private Set<Integer> getAttributeIds(SiteDTO site) {
        Set<Integer> ids = Sets.newHashSet();
        for (String propertyName : site.getPropertyNames()) {
            if (propertyName.startsWith(AttributeDTO.PROPERTY_PREFIX)) {
                int attributeId = AttributeDTO
                        .idForPropertyName(propertyName);
                boolean value = site.get(propertyName, false);
                if (value) {
                    ids.add(attributeId);
                }
            }
        }
        return ids;
    }
}
