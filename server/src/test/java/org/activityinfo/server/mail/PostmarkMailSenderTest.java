package org.activityinfo.server.mail;

import com.google.inject.util.Providers;
import freemarker.template.TemplateModelException;
import org.activityinfo.server.branding.Domain;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.util.TemplateModule;
import org.activityinfo.service.DeploymentConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class PostmarkMailSenderTest {

    private PostmarkMailSender sender;

    @Before
    public void setUp() throws TemplateModelException {
        Properties properties = new Properties();
        properties.setProperty(PostmarkMailSender.POSTMARK_API_KEY, "POSTMARK_API_TEST");
        DeploymentConfiguration config = new DeploymentConfiguration(properties);

        TemplateModule templateModule = new TemplateModule();

        sender = new PostmarkMailSender(config, templateModule.provideConfiguration(Providers.of(Domain.DEFAULT)));
    }

    @Test
    public void textEmail() {
        User user = new User();
        user.setChangePasswordKey("xyz123");
        user.setName("Alex");
        user.setEmail("akbertram@gmail.com");

        ResetPasswordMessage model = new ResetPasswordMessage(user);
        sender.send(model);
    }


}
