package com.ezeeshipping.eficaa.oms.outage.dashBoardServices;

import com.ezeeshipping.eficaa.oms.constants.IErrorConstants;
import com.ezeeshipping.eficaa.oms.core.BaseApplicationException;

public class DashBoardException extends BaseApplicationException {
	public DashBoardException() {
		super(IErrorConstants.NO_ERROR_MESSAGE);
	}

	public DashBoardException(String message) {
		super(message);
	}

	public DashBoardException(Exception exp) {
		super(exp);
	}
}
