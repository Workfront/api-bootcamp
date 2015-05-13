package com.workfront.api;
/*
 * Copyright (c) 2011 AtTask, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HostnameVerifier; // located in jsee.jar which is located in your jre/lib directory
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class StreamClient {

	private static final String METH_DELETE = "DELETE";
	private static final String METH_GET    = "GET";
	private static final String METH_POST   = "POST";
	private static final String METH_PUT    = "PUT";

	private static final String PATH_LOGIN  = "/login";
	private static final String PATH_LOGOUT = "/logout";
	private static final String PATH_SEARCH = "/search";

	private static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	public static final String NEW_LINE = "\r\n";

	private String hostname;
	private String apiKey;
	private String sessionID;

	public StreamClient (String hostname) {
		this.hostname = hostname;
	}

	public StreamClient (String hostname, String apiKey) {
		this.hostname = hostname;
		this.apiKey = apiKey;
	}

	public JSONObject login (String username, String password) throws StreamClientException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("password", password);
		return (JSONObject) request(PATH_LOGIN, params, null, METH_GET);
	}

	public boolean logout () throws StreamClientException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sessionID", sessionID);
		JSONObject result = (JSONObject) request(PATH_LOGOUT, params, null, METH_GET);
		try {
			return result.getBoolean("success");
		}
		catch (JSONException e) {
			throw new StreamClientException(e);
		}
	}

	public JSONObject uploadUrl(String url) throws StreamClientException {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("uri",url);
		return (JSONObject) request("/upload",query, null, METH_GET);
	}

	public JSONObject export (Map<String, Object> query) throws StreamClientException {
		return (JSONObject) request("/", query, null, METH_GET);
	}
	public JSONArray search (String objCode, Map<String, Object> query) throws StreamClientException {
		return search(objCode, query, (Set<String>) null);
	}

	public JSONArray search (String objCode, Map<String, Object> query, String[] fields) throws StreamClientException {
		return search(objCode, query, new HashSet<String>(Arrays.asList(fields)));
	}

	public JSONArray search (String objCode, Map<String, Object> query, Set<String> fields) throws StreamClientException {
		return (JSONArray) request("/"+objCode+PATH_SEARCH, query, fields, METH_GET);
	}

	public JSONObject get (String objCode, String objID) throws StreamClientException {
		return get(objCode, objID, (Set<String>) null);
	}

	public JSONObject get (String objCode, String objID, String[] fields) throws StreamClientException {
		return get(objCode, objID, new HashSet<String>(Arrays.asList(fields)));
	}

	public JSONObject get (String objCode, String objID, Set<String> fields) throws StreamClientException {
		return (JSONObject) request("/"+objCode+"/"+objID, null, fields, METH_GET);
	}

	public JSONObject post (String objCode, Map<String, Object> message) throws StreamClientException {
		return post(objCode, message, (Set<String>) null);
	}

	public JSONObject post (String objCode, Map<String, Object> message, String[] fields) throws StreamClientException {
		return post(objCode, message, new HashSet<String>(Arrays.asList(fields)));
	}

	public JSONObject post (String objCode, Map<String, Object> message, Set<String> fields) throws StreamClientException {
		return (JSONObject) request("/"+objCode, message, fields, METH_POST);
	}

	public JSONObject post (String objCode, Map<String, Object> params, Map<String, Object> updates, Set<String> fields) throws StreamClientException {
		params.put("updates", new JSONObject(updates).toString());
		return (JSONObject) request("/"+objCode, params, fields, METH_POST);
	}

	public JSONObject put (String objCode, String objID, Map<String, Object> message) throws StreamClientException {
		return put(objCode, objID, message, (Set<String>) null);
	}

	public JSONObject put (String objCode, String objID, Map<String, Object> message, String[] fields) throws StreamClientException {
		return put(objCode, objID, message, new HashSet<String>(Arrays.asList(fields)));
	}

	public JSONObject put (String objCode, String objID, Map<String, Object> message, Set<String> fields) throws StreamClientException {
		Map<String, Object> params = new HashMap<String, Object>();
		return put(objCode,objID,params,message,fields);
	}

	public JSONObject put (String objCode, String objID, Map<String, Object> params, Map<String, Object> updates, Set<String> fields) throws StreamClientException {
		params.put("updates", new JSONObject(updates).toString());
		return (JSONObject) request("/"+objCode+"/"+objID, params, fields, METH_PUT);
	}

	public boolean delete (String objCode, String objID) throws StreamClientException {
		return delete(objCode, objID, false);
	}

	public boolean delete (String objCode, String objID, boolean force) throws StreamClientException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("force", force);
		JSONObject result = (JSONObject) request("/"+objCode+"/"+objID, params, null, METH_DELETE);
		try {
			return result.getBoolean("success");
		}
		catch (JSONException e) {
			throw new StreamClientException(e);
		}
	}

	public JSONObject upload(File file) throws StreamClientException {
		String name = this.apiKey != null ? "apiKey" : "sessionID";
		String value = this.apiKey != null ? this.apiKey : this.sessionID;
		return (JSONObject) request("/upload?" + name + "=" + value, null, null, "POST", 0, file);
	}

    private Object request (String path, Map<String, Object> params, Set<String> fields, String method) throws StreamClientException {
        return request(path, params, fields, method, 0);
    }

	private Object request (String path, Map<String, Object> params, Set<String> fields, String method, int retryCount) throws StreamClientException {
		return request(path, params, fields, method, retryCount, null);
	}

	private Object request (String path, Map<String, Object> params, Set<String> fields, String method, int retryCount, File file) throws StreamClientException {
		HttpURLConnection conn = null;
        int responseCode = -1;

		try {
			String authenticationParam = "";
			if (apiKey != null) {
				authenticationParam += "apiKey=" + apiKey;
			} else if (sessionID  != null) {
				authenticationParam += "sessionID=" + sessionID;
			}
			String methodParam = "method=" + method;
			String query = authenticationParam + "&" + methodParam;

			if (params != null) {
				for (String key : params.keySet()) {
					if (params.get(key) instanceof String[]) {
						String[] paramVal = (String[]) params.get(key);
						for (int i = 0; i < paramVal.length; i++) {
							String val = paramVal[i];
							query += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
						}
					} else {
						query += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8");
					}
				}
			}

			if (fields != null) {
				query += "&fields=";
				for (String field : fields) {
					query += URLEncoder.encode(field, "UTF-8") + ",";
				}
				query = query.substring(0, query.lastIndexOf(","));
			}

			conn = createConnection(hostname + path, method);

			String boundary = Long.toHexString(System.currentTimeMillis());

			if (file != null) {
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
				conn.setRequestProperty("User-Agent", "Workfront Java StreamClient");
			}

			// Send request
			OutputStream outputStream = conn.getOutputStream();
			Writer out = new OutputStreamWriter(outputStream);
			if (file != null) {
				addFileToRequest(boundary, out, outputStream, file);
			} else {
				out.write(query);
			}

			out.flush();
			out.close();

            // Get response code
            responseCode = conn.getResponseCode();

			// Read response
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = in.readLine()) != null) {
				response.append(line);
			}

			in.close();

			// Decode JSON
			JSONObject result = new JSONObject(response.toString());

			// Verify result
			if (result.has("error")) {
				throw new StreamClientException(result.getJSONObject("error").getString("message"));
			}
			else if (!result.has("data")) {
				throw new StreamClientException("Invalid response from server");
			}

			// Manage the session
			if (path.equals(PATH_LOGIN)) {
				sessionID = result.getJSONObject("data").getString("sessionID");
			}
			else if (path.equals(PATH_LOGOUT)) {
				sessionID = null;
			}

			return result.get("data");
        } catch(ConnectException connectException) {
            throw new StreamClientException("Unable to connect to " + hostname + path);
		} catch (IOException e) {
			//getErrorStream() can return null if no error data was sent back
            if (conn.getErrorStream() != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                try {
                    copyStream(conn.getErrorStream(), out);
                } catch (IOException e1) {
                    // Removed printStackTrace call
                }

                throw new StreamClientException(new String(out.toByteArray()));
            } else {
                //I believe this use case happens when the we are sending to many requests at one time...
                if (retryCount < 3) {
                    return request(path, params, fields, method, ++retryCount);
                } else {
                    throw new StreamClientException("An error happened but no error data was sent... 3 time... Response Code = " + responseCode);
                }
            }
		} catch (Exception e) {
			throw new StreamClientException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private void copyStream(InputStream in, OutputStream out) throws IOException {
		try {
			byte[] buffer = new byte[8192];
			int count;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
		} finally {
			in.close();
			out.close();
		}
	}

	private HttpURLConnection createConnection (String spec, String method) throws IOException {
		URL url = new URL(spec);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);
		}

		conn.setAllowUserInteraction(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(60000);
		conn.setReadTimeout(300000);

		return conn;
	}

	private void addFileToRequest(String boundary, Writer out, OutputStream binaryStream, File file) throws IOException {
		out.append("--" + boundary).append(NEW_LINE);
		out.append("Content-Disposition: form-data; name=\"uploadedFile\"; filename=\"" + file.getName() + "\"").append(NEW_LINE);
		out.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(NEW_LINE);
		out.append("Content-Transfer-Encoding: binary").append(NEW_LINE).append(NEW_LINE);
		out.flush();

		FileInputStream inputStream = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			binaryStream.write(buffer, 0, bytesRead);
		}
		binaryStream.flush();
		inputStream.close();

		out.append(NEW_LINE);
		out.flush();
		out.append("--").append(boundary).append("--").append(NEW_LINE);
	}
}
