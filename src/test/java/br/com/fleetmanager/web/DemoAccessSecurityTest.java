package br.com.fleetmanager.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.security.demo.enabled=true")
class DemoAccessSecurityTest {

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    void loginPageOffersDemoAccessWithoutExposingCredentials() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Acessar demonstração")))
            .andExpect(content().string(not(containsString("visitante-demo"))));
    }

    @Test
    void demoLoginRequiresCsrfProtection() throws Exception {
        mockMvc.perform(post("/demo-login"))
            .andExpect(status().isForbidden());
    }

    @Test
    void demoLoginCreatesReadOnlySession() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/demo-login").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/").session(session))
            .andExpect(status().isOk())
            .andExpect(authenticated().withUsername("visitante-demo").withRoles("DEMO"))
            .andExpect(content().string(containsString("Modo demonstração")))
            .andExpect(content().string(not(containsString("+ Novo veículo"))));
    }

    @ParameterizedTest(name = "demo can view {0}")
    @ValueSource(strings = {"/", "/vehicles", "/drivers", "/taxes", "/maintenances"})
    void demoCanViewReadOnlyPages(String path) throws Exception {
        mockMvc.perform(get(path).with(user("visitante-demo").roles("DEMO")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Modo demonstração")));
    }

    @Test
    void demoVehicleListDoesNotRenderMutationControls() throws Exception {
        mockMvc.perform(get("/vehicles").with(user("visitante-demo").roles("DEMO")))
            .andExpect(status().isOk())
            .andExpect(content().string(not(containsString("+ Novo veículo"))))
            .andExpect(content().string(not(containsString("Editar"))))
            .andExpect(content().string(not(containsString("Excluir"))));
    }

    @ParameterizedTest(name = "demo cannot open {0}")
    @ValueSource(strings = {
        "/vehicles/new",
        "/drivers/new",
        "/taxes/new",
        "/maintenances/new"
    })
    void demoCannotOpenMutationForms(String path) throws Exception {
        mockMvc.perform(get(path).with(user("visitante-demo").roles("DEMO")))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "demo cannot submit {0}")
    @ValueSource(strings = {"/vehicles", "/drivers", "/taxes", "/maintenances"})
    void demoCannotSubmitMutations(String path) throws Exception {
        mockMvc.perform(post(path)
                .with(user("visitante-demo").roles("DEMO"))
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @ParameterizedTest(name = "demo cannot modify through {0}")
    @ValueSource(strings = {
        "/vehicles/1",
        "/vehicles/1/delete",
        "/drivers/1",
        "/drivers/1/delete",
        "/taxes/1",
        "/taxes/1/delete",
        "/maintenances/1",
        "/maintenances/1/delete"
    })
    void demoCannotModifyExistingRecords(String path) throws Exception {
        mockMvc.perform(post(path)
                .with(user("visitante-demo").roles("DEMO"))
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    void administratorKeepsAccessToMutationForms() throws Exception {
        mockMvc.perform(get("/vehicles/new").with(user("admin-teste").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Salvar veículo")));
    }
}
