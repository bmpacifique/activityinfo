package org.activityinfo.ui.component.form.field.image;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.image.ImageRowValue;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 8/12/14.
 */
public class ImageUploadRow extends Composite {
    private static final int THUMBNAIL_SIZE = 24;

    interface OurUiBinder extends UiBinder<FormPanel, ImageUploadRow> {
    }

    private static OurUiBinder ourUiBinder = GWT.create(OurUiBinder.class);

    private static final Logger LOGGER = Logger.getLogger(ImageUploadRow.class.getName());

    private final ImageRowValue value;
    private final String fieldId;
    private final String resourceId;
    private boolean readOnly;
    private HandlerRegistration oldHandler;

    @UiField
    FileUpload fileUpload;
    @UiField
    HTMLPanel imageContainer;
    @UiField
    ImageElement loadingImage;
    @UiField
    Image thumbnail;
    @UiField
    Button downloadButton;
    @UiField
    Button removeButton;
    @UiField
    VerticalPanel formFieldsContainer;
    @UiField
    Button addButton;
    @UiField
    FormPanel formPanel;

    public ImageUploadRow(ImageRowValue value, String fieldId, String resourceId) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.value = value;
        this.fieldId = fieldId;
        this.resourceId = resourceId;

        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                requestUploadUrl();
            }
        });
        downloadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                download();
            }
        });

        if (value.getBlobId() != null) {
            imageContainer.setVisible(false);
            downloadButton.setVisible(true);
            thumbnail.setVisible(true);
            thumbnail.setUrl(buildUrl(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        fileUpload.setEnabled(!readOnly);
        downloadButton.setEnabled(!readOnly);
        removeButton.setEnabled(!readOnly);
        addButton.setEnabled(!readOnly);
    }

    private String createUploadUrl() {
        String blobId = Resources.generateId().asString();
        String fileName = fileName();
        String mimeType = MimeTypeUtil.mimeTypeFromFileExtension(fileExtension(fileName));
        value.setMimeType(mimeType);
        value.setFilename(fileName);
        value.setBlobId(blobId);
        return "/service/blob/credentials/" + blobId;
    }

    public static String fileExtension(String filename) {
        int i = filename.lastIndexOf(".");
        if (i != -1) {
            return filename.substring(i + 1);
        }
        return filename;
    }

    private String fileName() {
        final String filename = fileUpload.getFilename();
        if (Strings.isNullOrEmpty(filename)) {
            return "unknown";
        }

        int i = filename.lastIndexOf("/");
        if (i == -1) {
            i = filename.lastIndexOf("\\");
        }
        if (i != -1 && (i + 1) < filename.length()) {
            return filename.substring(i + 1);
        }
        return filename;
    }

    private void requestUploadUrl() {
        try {
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, URL.encode(createUploadUrl()));
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // Remove the old hidden fields before adding the new ones
                    List<Hidden> hidden = Lists.newArrayListWithCapacity(formFieldsContainer.getWidgetCount());
                    for (int i = 0; i < formFieldsContainer.getWidgetCount(); i++) {
                        Widget widget = formFieldsContainer.getWidget(i);
                        if (widget instanceof Hidden) {
                            hidden.add((Hidden) widget);
                        }
                    }
                    // We can't just iterate once using the getWidget() method because removing a widget changes indexes
                    for (Hidden old : hidden) {
                        formFieldsContainer.remove(old);
                    }

                    String json = response.getText();
                    JSONObject credentials = JSONParser.parseStrict(json).isObject();
                    JSONObject formFields = credentials.get("formFields").isObject();
                    for (String fieldName : formFields.keySet()) {
                        formFieldsContainer.add(new Hidden(fieldName, formFields.get(fieldName).isString().stringValue()));
                    }

                    formPanel.setAction(credentials.get("url").isString().stringValue());
                    formPanel.setMethod(credentials.get("method").isString().stringValue());
                    upload();
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    LOGGER.log(Level.SEVERE, "Failed to send request", exception);
                }
            });
        } catch (RequestException e) {
            LOGGER.log(Level.SEVERE, "Failed to send request", e);
        }
    }

    private void upload() {
        imageContainer.setVisible(true);
        downloadButton.setVisible(false);

        if (oldHandler != null) oldHandler.removeHandler();
        oldHandler = formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                String responseString = event.getResults(); // what about fail results?

                imageContainer.setVisible(false);
                downloadButton.setVisible(true);
                thumbnail.setVisible(true);
                thumbnail.setUrl(buildUrl(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
            }
        });
        formPanel.submit();
    }

    private void download() {
        Window.open(buildUrl(value.getWidth(), value.getHeight()), "_blank", null);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public ImageRowValue getValue() {
        return value;
    }

    private String buildUrl(int width, int height) {
        StringBuilder stringBuilder = new StringBuilder("/service/blob/");
        stringBuilder.append(resourceId);
        stringBuilder.append("/");
        stringBuilder.append(fieldId);
        stringBuilder.append("/");
        stringBuilder.append(value.getBlobId());
        stringBuilder.append("/thumbnail?width=");
        stringBuilder.append(width);
        stringBuilder.append("&height=");
        stringBuilder.append(height);
        return stringBuilder.toString();
    }
}