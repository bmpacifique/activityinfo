package org.activityinfo.server.command.handler;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.adapter.ActivityFormClassBuilder;
import org.activityinfo.legacy.shared.command.GetActivityForm;
import org.activityinfo.legacy.shared.command.GetFormClass;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.FormClassResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.exception.UnexpectedCommandException;
import org.activityinfo.legacy.shared.model.ActivityFormDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.server.command.DispatcherSync;
import org.activityinfo.server.command.handler.json.JsonHelper;
import org.activityinfo.server.database.hibernate.entity.Activity;
import org.activityinfo.server.database.hibernate.entity.FormClassEntity;
import org.activityinfo.server.database.hibernate.entity.User;

import javax.inject.Provider;
import javax.persistence.EntityManager;

public class GetFormClassHandler implements CommandHandler<GetFormClass> {

    private Provider<EntityManager> entityManager;
    private DispatcherSync dispatcherSync;

    @Inject
    public GetFormClassHandler(Provider<EntityManager> entityManager, DispatcherSync dispatcherSync) {
        this.entityManager = entityManager;
        this.dispatcherSync = dispatcherSync;
    }

    @Override
    public CommandResult execute(GetFormClass cmd, User user) throws CommandException {
        return new FormClassResult(fetchJson(cmd));
    }

    private String fetchJson(GetFormClass cmd) {
        char domain = ResourceId.valueOf(cmd.getResourceId()).getDomain();
        if (domain == CuidAdapter.ACTIVITY_DOMAIN) {
            Activity activity = entityManager.get().find(Activity.class, CuidAdapter.getLegacyIdFromCuid(cmd.getResourceId()));
            ActivityFormDTO activityDTO = dispatcherSync.execute(new GetActivityForm(activity.getId()));

            String json = JsonHelper.readJson(activity);
            if (json == null) {
                json = constructFromLegacy(activityDTO);
            }
            json = fixIfNeeded(json, activityDTO);
            return json;
        } else {
            FormClassEntity hibernateFormClass =
                    entityManager.get().find(FormClassEntity.class, cmd.getResourceId());
            return JsonHelper.readJson(hibernateFormClass);
        }
    }


    private String constructFromLegacy(final ActivityFormDTO activityDTO) {

        ActivityFormClassBuilder builder = new ActivityFormClassBuilder(activityDTO);
        FormClass formClass = builder.build();
        return Resources.toJson(formClass.asResource());
    }

    // AI-1057 - Fix forms corrupted during cloning, partner and project range must reference to db id instead of partner id.
    private String fixIfNeeded(String json, ActivityFormDTO activityDTO) {

        boolean hasPartner = false;
        boolean hasProject = false;
        boolean hasStartDate = false;
        boolean hasEndDate = false;

        FormClass formClass = FormClass.fromResource(Resources.fromJson(json));
        for (FormField formField : formClass.getFields()) {
            int fieldIndex = CuidAdapter.getBlockSilently(formField.getId(), 1);
            if (fieldIndex == CuidAdapter.PARTNER_FIELD) {
                ReferenceType sourceType = (ReferenceType) formField.getType();

                formField.setType(new ReferenceType()
                        .setCardinality(sourceType.getCardinality())
                        .setRange(CuidAdapter.partnerFormClass(activityDTO.getDatabaseId())));
                hasPartner = true;
            } else if (fieldIndex == CuidAdapter.PROJECT_FIELD) {
                ReferenceType sourceType = (ReferenceType) formField.getType();

                formField.setType(new ReferenceType()
                        .setCardinality(sourceType.getCardinality())
                        .setRange(CuidAdapter.projectFormClass(activityDTO.getDatabaseId())));

                hasProject = true;
            } else if (fieldIndex == CuidAdapter.START_DATE_FIELD) {
                hasStartDate = true;
            } else if (fieldIndex == CuidAdapter.END_DATE_FIELD) {
                hasEndDate = true;
            }

        }

        if (!hasPartner) {
            formClass.addElement(ActivityFormClassBuilder.createPartnerField(formClass.getId(), activityDTO));
        }
        if (!hasStartDate) {
            formClass.addElement(ActivityFormClassBuilder.createStartDateField(formClass.getId()));
        }
        if (!hasEndDate) {
            formClass.addElement(ActivityFormClassBuilder.createEndDateField(formClass.getId()));
        }
        if (!hasProject) {
            formClass.addElement(ActivityFormClassBuilder.createProjectField(formClass.getId(), activityDTO));
        }

        return Resources.toJson(formClass.asResource());
    }
}
