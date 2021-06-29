package org.upgrad.upstac.testrequests;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class LabRequestControllerTest.
 */
@SpringBootTest

/** The Constant log. */
@Slf4j
class LabRequestControllerTest {

	/** The lab request controller. */
	@InjectMocks
	LabRequestController labRequestController;
	
	/** The test request update service. */
	@Mock
	TestRequestUpdateService testRequestUpdateService;

	/** The test request query service. */
	@Mock
	TestRequestQueryService testRequestQueryService;
	
	/** The user logged in service. */
	@Mock
	UserLoggedInService userLoggedInService;

	/**
	 * Calling assign for lab test with valid test request id should update the request status.
	 */
	@Test
	public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;
		TestRequest response = getMockedResponseFrom();

		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.assignForLabTest(testRequestId, user)).thenReturn(response);

		// Act
		TestRequest assignedTestRequest = labRequestController.assignForLabTest(testRequestId);

		// Assert
		assertNotNull(assignedTestRequest);
		assertEquals(assignedTestRequest, response);

	}


	
	/**
	 * Calling assign for lab test with valid test request id should throw exception.
	 */
	@Test
	public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;

		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.assignForLabTest(testRequestId, user))
				.thenThrow(new AppException("Invalid data"));

		// Act
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {

			labRequestController.assignForLabTest(testRequestId);
		});

		// Assert
		assertNotNull(result);
		assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
		assertEquals("Invalid data", result.getReason());

	}

	/**
	 * Calling update lab test with valid test request id should update the request status and update test request details.
	 */
	@Test
	public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;
		TestRequest response = getMockedResponseFrom();
		CreateLabResult createLabResult = new CreateLabResult();
		
		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.updateLabTest(testRequestId,createLabResult, user)).thenReturn(response);

		// Act
		TestRequest assignedTestRequest = labRequestController.updateLabTest(testRequestId,createLabResult);

		// Assert
		assertNotNull(assignedTestRequest);
		assertEquals(assignedTestRequest, response);

	}

	/**
	 * Calling update lab test with invalid test request id should throw exception.
	 */
	@Test
	public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {

		// Arrage
		User user = createUser();
		Long testRequestId = 2L;
		CreateLabResult createLabResult = new CreateLabResult();

		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestUpdateService.updateLabTest(testRequestId,createLabResult, user))
		.thenThrow(new AppException("Invalid data"));

		// Act
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {

			labRequestController.updateLabTest(testRequestId,createLabResult);
		});

		// Assert
		assertNotNull(result);
		assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
		assertEquals("Invalid data", result.getReason());

	}

	
	/**
	 * Calling get for tests returns valid expect same as response.
	 */
	@Test
	public void calling_getForTests_returns_valid_expect_same_as_response() {

		List<TestRequest> testRequestLists = new ArrayList<>();
		Mockito.when(testRequestQueryService.findBy(Mockito.any())).thenReturn(testRequestLists);
		
		List<TestRequest> response = labRequestController.getForTests();
		
		// Assert
		assertNotNull(response);
		assertEquals(testRequestLists, response);
	}
	
	
	/**
	 * Calling get for tester returns valid expect same as response.
	 */
	@Test
	public void calling_getForTester_returns_valid_expect_same_as_response() {
		User user = createUser();
		
		List<TestRequest> testRequestLists = new ArrayList<>();
		Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
		Mockito.when(testRequestQueryService.findByTester(Mockito.any())).thenReturn(testRequestLists);
		
		List<TestRequest> response = labRequestController.getForTester();
		
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