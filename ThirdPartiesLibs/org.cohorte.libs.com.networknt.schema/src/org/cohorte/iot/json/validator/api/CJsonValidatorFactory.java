/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cohorte.iot.json.validator.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.cohorte.iot.json.validator.api.CJsonValidatorDefault;
import org.cohorte.iot.json.validator.api.IValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.SpecVersionDetector;

/**
 * Created by steve on 22/10/16.
 */
public class CJsonValidatorFactory {

	private static final Object lock = new Object();
	private static CJsonValidatorFactory pFactory;
	private static IValidator pSingleton;

	public static CJsonValidatorFactory getFactory() {
		synchronized (lock) {
			if (pFactory == null) {
				pFactory = new CJsonValidatorFactory();
			}
		}
		return pFactory;
	}

	public static IValidator getSingleton() {
		synchronized (lock) {
			if (pSingleton == null) {
				pSingleton = new CJsonValidatorDefault();
			}
		}
		return pSingleton;
	}

	private final ObjectMapper mapper = new ObjectMapper();
	private CJsonValidatorFactory() {

	}
	public JsonNode getJsonNodeFromClasspath(final String name) throws IOException {
		final InputStream is1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(name);
		return mapper.readTree(is1);
	}

	public JsonNode getJsonNodeFromStringContent(final String content) throws IOException {
		return mapper.readTree(content);
	}

	public JsonNode getJsonNodeFromUrl(final String url) throws IOException {
		return mapper.readTree(new URL(url));
	}

	public JsonSchema getJsonSchemaFromClasspath(final String name) {
		final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
		final InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(name);
		return factory.getSchema(is);
	}

	public JsonSchema getJsonSchemaFromJsonNode(final JsonNode jsonNode) {
		final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
		return factory.getSchema(jsonNode);
	}

	// Automatically detect version for given JsonNode
	public JsonSchema getJsonSchemaFromJsonNodeAutomaticVersion(final JsonNode jsonNode) {
		final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonNode));
		return factory.getSchema(jsonNode);
	}

	public JsonSchema getJsonSchemaFromStringContent(final String schemaContent) {
		final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
		return factory.getSchema(schemaContent);
	}

	public JsonSchema getJsonSchemaFromUrl(final String uri) throws URISyntaxException {
		final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
		return factory.getSchema(new URI(uri));
	}

}