package org.upgrad.upstac.testrequests.consultation;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

/**
 * The Class ConsultationController.
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

	/** The log. */
	Logger log = LoggerFactory.getLogger(ConsultationController.class);

	/** The test request update service. */
	@Autowired
	private TestRequestUpdateService testRequestUpdateService;

	/** The test request query service. */
	@Autowired
	private TestRequestQueryService testRequestQueryService;

	/** The test request flow service. */
	@Autowired
	TestRequestFlowService testRequestFlowService;

	/** The user logged in service. */
	@Autowired
	private UserLoggedInService userLoggedInService;

	/**
	 * Gets the for consultations.
	 *
	 * @return the for consultations
	 */
	@GetMapping("/in-queue")
	@PreAuthorize("hasAnyRole('DOCTOR')")
	public List<TestRequest> getForConsultations() {

		return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

	}

	/**
	 * Gets the for doctor.
	 *
	 * @return the for doctor
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DOCTOR')")
	public List<TestRequest> getForDoctor() {

		User doctor = userLoggedInService.getLoggedInUser();

		return testRequestQueryService.findByDoctor(doctor);

	}

	/**
	 * Assign for consultation.
	 *
	 * @param id the id
	 * @return the test request
	 */
	@PreAuthorize("hasAnyRole('DOCTOR')")
	@PutMapping("/assign/{id}")
	public TestRequest assignForConsultation(@PathVariable Long id) {

		try {
			User doctor = userLoggedInService.getLoggedInUser();

			return testRequestUpdateService.assignForConsultation(id, doctor);

		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}

	/**
	 * Update consultation.
	 *
	 * @param id the id
	 * @param testResult the test result
	 * @return the test request
	 */
	@PreAuthorize("hasAnyRole('DOCTOR')")
	@PutMapping("/update/{id}")
	public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {

		try {

			User doctor = userLoggedInService.getLoggedInUser();

			return testRequestUpdateService.updateConsultation(id, testResult, doctor);

		} catch (ConstraintViolationException e) {
			throw asConstraintViolation(e);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}

}
