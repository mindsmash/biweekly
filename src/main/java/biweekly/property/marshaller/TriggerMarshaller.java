package biweekly.property.marshaller;

import java.util.Date;
import java.util.List;

import biweekly.io.CannotParseException;
import biweekly.parameter.ICalParameters;
import biweekly.parameter.Value;
import biweekly.property.Trigger;
import biweekly.util.Duration;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Marshals {@link Trigger} properties.
 * @author Michael Angstadt
 */
public class TriggerMarshaller extends ICalPropertyMarshaller<Trigger> {
	public TriggerMarshaller() {
		super(Trigger.class, "TRIGGER");
	}

	@Override
	protected void _prepareParameters(Trigger property, ICalParameters copy) {
		Value value = (property.getDate() == null) ? null : Value.DATE_TIME;
		copy.setValue(value);
	}

	@Override
	protected String _writeText(Trigger property) {
		if (property.getDate() != null) {
			return date(property.getDate()).write();
		}
		if (property.getDuration() != null) {
			return property.getDuration().toString();
		}
		return "";
	}

	@Override
	protected Trigger _parseText(String value, ICalParameters parameters, List<String> warnings) {
		value = unescape(value);

		try {
			Date date = date(value).tzid(parameters.getTimezoneId(), warnings).parse();
			return new Trigger(date);
		} catch (IllegalArgumentException e) {
			//unable to parse value as date, must be a duration
		}

		try {
			return new Trigger(Duration.parse(value), parameters.getRelated());
		} catch (IllegalArgumentException e) {
			//unable to parse duration
		}

		throw new CannotParseException("Could not parse value as a date or duration.");
	}
}