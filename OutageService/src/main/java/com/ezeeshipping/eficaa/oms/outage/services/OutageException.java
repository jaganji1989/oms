package com.ezeeshipping.eficaa.oms.outage.services;

import com.ezeeshipping.eficaa.oms.constants.IErrorConstants;
import com.ezeeshipping.eficaa.oms.core.BaseApplicationException;

public class OutageException extends BaseApplicationException {
	public OutageException() {
		super(IErrorConstants.NO_ERROR_MESSAGE);
	}

	public OutageException(String message) {
		super(message);
	}

	public OutageException(Exception exp) {
		super(exp);
	}
}
