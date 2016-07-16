package com.github.crehn.pantarhei.boundary;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.Builder;
import lombok.Value;

@Provider
public class AnnotatedExceptionMapper implements ExceptionMapper<Exception> {
	private static final String CONTENT_TYPE = "application/problem+json";
	private static final String URN_PROBLEM_PREFIX = "urn:problem:";

	@Value
	@Builder
	public static class Problem {
		private static final String URN_PROBLEM_INSTANCE_PREFIX = "urn:problem-instance:";

		/**
		 * SHOULD be stable across versions. SHOULD identify a problem class uniquely.
		 */
		URI type;

		/**
		 * A short, human-readable summary of the problem type. It SHOULD NOT change from occurrence to occurrence of
		 * the problem, except for purposes of localization.
		 */
		String title;

		/**
		 * The HTTP status code generated by the origin server for this occurrence of the problem.
		 */
		StatusType status;

		/**
		 * The full, human-readable explanation specific to this occurrence of the problem. It MAY change from
		 * occurrence to occurrence of the problem.
		 */
		String detail;

		/**
		 * A URI reference that identifies the specific occurrence of the problem. It may or may not yield further
		 * information if dereferenced.
		 */
		URI instance = URI.create(URN_PROBLEM_INSTANCE_PREFIX + UUID.randomUUID());
	}

	@Override
	public Response toResponse(Exception exception) {
		MapToProblem annotation = exception.getClass().getAnnotation(MapToProblem.class);
		if (annotation != null)
			return Response //
			        .status(annotation.status()) //
			        .type(CONTENT_TYPE) //
			        .entity(toProblem(annotation, exception)) //
			        .build();
		else
			return Response //
			        .status(INTERNAL_SERVER_ERROR) //
			        .type(CONTENT_TYPE) //
			        .entity(defaultProblem(exception)) //
			        .build();
	}

	private Problem toProblem(MapToProblem annotation, Exception exception) {
		return Problem.builder() //
		        .type(URI.create(URN_PROBLEM_PREFIX + exception.getClass().getName())) //
		        .title(annotation.title()) //
		        .status(annotation.status()) //
		        .detail(exception.getMessage()) //
		        .build();
	}

	private Problem defaultProblem(Exception exception) {
		return Problem.builder() //
		        .type(URI.create(URN_PROBLEM_PREFIX + exception.getClass().getName())) //
		        .title("a problem occured") //
		        .status(INTERNAL_SERVER_ERROR) //
		        .detail(exception.getMessage()) //
		        .build();
	}
}
