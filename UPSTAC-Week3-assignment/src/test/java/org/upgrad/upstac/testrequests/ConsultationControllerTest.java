package org.upgrad.upstac.testrequests;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;


/**
 * The Class ConsultationControllerTest.
 */
@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

	/** The consultation controller. */
	@InjectMocks
	ConsultationController consultationController;

	/** The test request query service. */
	@Mock
	TestRequestQueryService testRequestQueryService;
	
	/** The test request update service. */
	@Mock
	TestRequestUpdateService testRequestUpdateService;
	
	/** The user logged in service. */
	@Mock
	UserLoggedInService userLoggedInService;

	/**
	 * Calling assign for consultation with valid test request id should update the request status.
	 */
	@Test
	public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status() {

		//Arrage
		User user= createUser();
		Long testRequestId = 2L;
		TestRequest response = getMockedResponseFrom();
		
		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.assignForConsultation(testRequestId,user)).thenReturn(response);
		
		//Act
		TestRequest assignedTestRequest = consultationController.assignForConsultation(testRequestId);

		//Assert
		assertNotNull(assignedTestRequest);
        assertEquals(assignedTestRequest,response);

	}


	/**
	 * Calling assign for consultation with valid test request id should throw exception.
	 */
	@Test
	public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;

		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.assignForConsultation(testRequestId, user)).thenThrow(new AppException("Invalid data"));

		// Act
		ResponseStatusException result = assertThrows(ResponseStatusException.class,()->{

			consultationController.assignForConsultation(testRequestId);
        });

		// Assert
		assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals("Invalid data",result.getReason());
	}

	/**
	 * Calling update consultation with valid test request id should update the request status and update consultation details.
	 */
	@Test
	public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;
		TestRequest response = getMockedResponseFrom();
		CreateConsultationRequest consultationRequest = new CreateConsultationRequest();

		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.updateConsultation(testRequestId, consultationRequest, user))
				.thenReturn(response);

		// Act
		TestRequest assignedTestRequest = consultationController.updateConsultation(testRequestId, consultationRequest);

		// Assert
		assertNotNull(assignedTestRequest);
		assertEquals(assignedTestRequest, response);

	}

	/**
	 * Calling update consultation with invalid test request id should throw exception.
	 */
	@Test
	public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;
		CreateConsultationRequest consultationRequest = new CreateConsultationRequest();
		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.updateConsultation(testRequestId,consultationRequest, user))
				.thenThrow(new AppException("Invalid data"));

		// Act
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {

			consultationController.updateConsultation(testRequestId,consultationRequest);
		});

		// Assert
		assertNotNull(result);
		assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
		assertEquals("Invalid data", result.getReason());
	}

	
	/**
	 * Calling get for consultations returns valid expect same as response.
	 */
	@Test
	public void calling_getForConsultations_returns_valid_expect_same_as_response() {
		
		List<TestRequest> testRequestLists = new ArrayList<>();
		Mockito.when(testRequestQueryService.findBy(Mockito.any())).thenReturn(testRequestLists);
		
		List<TestRequest> response = consultationController.getForConsultations();
		
		// Assert
		assertNotNull(response);
		assertEquals(testRequestLists, response);
		

	}


	/**
	 * Calling get for doctor returns valid expect same as response.
	 */
	@Test
	public void calling_getForDoctor_returns_valid_expect_same_as_response() {
		
		List<TestRequest> testRequestLists = new ArrayList<>();
		
		Mockito.when(testRequestQueryService.findByDoctor(Mockito.any())).thenReturn(testRequestLists);
		
		List<TestRequest> response = consultationController.getForDoctor();
		
		// Assert
		assertNotNull(response);
		assertEquals(testRequestLists, response);
		

	}
	
	/**
	 * Creates the user.
	 *
	 * @return the user
	 */
	private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("someDoctor");
        return user;
    }
	
	/**
	 * Gets the mocked response from.
	 *
	 * @return the mocked response from
	 */
	public TestRequest getMockedResponseFrom() {
        TestRequest testRequest = new TestRequest();

        testRequest.setName("someuser");
        testRequest.setCreated(LocalDate.now());
        testRequest.setStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        testRequest.setAge(70);
        testRequest.setEmail("someone" + "123456789" + "@somedomain.com");
        testRequest.setPhoneNumber("123456789");
        testRequest.setPinCode(716768);
        testRequest.setAddress("some Addres");
        testRequest.setGender(Gender.MALE);

        testRequest.setCreatedBy(createUser());

        return testRequest;
    }
}