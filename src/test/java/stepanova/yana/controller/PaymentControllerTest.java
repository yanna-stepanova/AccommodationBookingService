package stepanova.yana.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.model.Status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext context) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/payment/controller/"
                            + "add-entities-in-all-tables.sql"));
        }
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/clear-all-tables.sql"));
        }
    }

    @Test
    @DisplayName("Create payment session")
    @WithUserDetails("user@gmail.com")
    @Sql(scripts = "classpath:database/payment/controller/clear-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createPayment_GivenValidRequestDto_Success() throws Exception {
        //Given
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(2L);

        PaymentDto expected = new PaymentDto();
        expected.setStatus(Status.PENDING);
        expected.setBookingId(requestDto.bookingId());
        expected.setAmountToPay(BigDecimal.valueOf(98.05));

        //When
        MvcResult result = mockMvc.perform(post("/payments")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        PaymentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "id", "dateTimeCreated", "accommodation", "sessionUrl", "sessionID"));
    }

    @Test
    @DisplayName("Pay a booking by sessionId")
    @WithUserDetails("user@gmail.com")
    @Sql(scripts = "classpath:database/payment/controller/add-entity-in-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/controller/clear-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getSuccess_GivenValidSessionId_Success() throws Exception {
        //Given
        String sessionId = "cs_test_a11G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou";

        // When
        MvcResult result = mockMvc.perform(get("/payments/success")
                        .param("sessionId", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        PaymentDto expected = new PaymentDto();
        expected.setId(10L);
        expected.setBookingId(1L);
        expected.setStatus(Status.PAID);
        expected.setSessionID(sessionId);
        expected.setAmountToPay(BigDecimal.valueOf(100.05));

        PaymentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "dateTimeCreated", "accommodation", "sessionUrl"));
    }

    @Test
    @DisplayName("Cancel payment by sessionId")
    @WithUserDetails("user@gmail.com")
    @Sql(scripts = "classpath:database/payment/controller/add-entity-in-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/controller/clear-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCancelGivenValidSessionId_Success() throws Exception {
        //Given
        String sessionId = "cs_test_a11G0LTvDQ5zGNTskpvipghM01gQGLrS7B5B9RVchXD6PlmH5sBfuq4Kou";

        // When
        MvcResult result = mockMvc.perform(get("/payments/cancel")
                        .param("sessionId", sessionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        String expected = String.format("Payment for sessionId = %s can be made later"
                + " (but the session is available for only 24 hours)", sessionId);
        String actual = result.getResponse().getContentAsString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get all payments by userId")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/payment/controller/add-entity-in-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/controller/clear-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAllPaymentByUser_GivenUserId_Success() throws Exception {
        //Given
        Long userId = 2L;

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(10L);
        paymentDto.setBookingId(1L);
        paymentDto.setStatus(Status.PENDING);
        paymentDto.setAmountToPay(BigDecimal.valueOf(100.05));

        //When
        MvcResult result = mockMvc.perform(get("/payments")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        List<PaymentDto> expected = List.of(paymentDto);

        PaymentDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                    expected.get(i), actual[i], "dateTimeCreated",
                    "accommodation", "sessionUrl", "sessionID"));
        }
    }

    @Test
    @DisplayName("Get all payments for owner")
    @WithUserDetails("user@gmail.com")
    @Sql(scripts = "classpath:database/payment/controller/add-entity-in-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/payment/controller/clear-table-payments.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getPayments_GivenValidUser_Success() throws Exception {
        //Given & When
        MvcResult result = mockMvc.perform(get("/payments/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //Then
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(10L);
        paymentDto.setBookingId(1L);
        paymentDto.setStatus(Status.PENDING);
        paymentDto.setAmountToPay(BigDecimal.valueOf(100.05));
        List<PaymentDto> expected = List.of(paymentDto);

        PaymentDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), PaymentDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                    expected.get(i), actual[i], "dateTimeCreated",
                    "accommodation", "sessionUrl", "sessionID"));
        }
    }
}
