package dkarobo.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class BotViaRest implements Bot {

	private String webAddress;
	private final static Logger log = LoggerFactory.getLogger(BotViaRest.class);
	private String[] lastPlan;

	public BotViaRest(String webAddress) {
		this.webAddress = webAddress;
	}

	public String getWebAddress() {
		return webAddress;
	}

	@Override
	public Coordinates whereAreYou() {
		try {
			log.trace("Calling {}{}", getWebAddress() , "/whereareyou");
			URLConnection connection = new URL(webAddress + "/whereareyou").openConnection();
			String json = IOUtils.toString(connection.getInputStream());
			log.trace("{}", json);
			Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
			String x = JsonPath.read(document, "$.current_position.x");
			String y = JsonPath.read(document, "$.current_position.y");
			String z = JsonPath.read(document, "$.current_position.theta");
			return Position.create(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// do better than that
		throw new RuntimeException();
	}

	@Override
	public boolean isBusy() throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(webAddress + "/do").openConnection();
		return connection.getResponseCode() == 200;
	}

	@Override
	public String[] getPlan() {
		return lastPlan;
	}

	@Override
	public void sendPlan(String[] thePlan) throws BusyBotException {
		log.info("Send to Bot: {}", thePlan);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("[");
		for (String s : thePlan) {
			if (first) {	
				first = false;
			} else {
				sb.append(',');
			}
			sb.append(s);
		}
		sb.append("]");
		sendPlan(sb.toString());
	}

	private void sendPlan(final String plan) throws BusyBotException {
		try {
			String parameters = "p=" + URLEncoder.encode(plan, "UTF-8");
			log.debug("Plan: {}", plan ) ; //parameters);
			byte[] postDataBytes = parameters.toString().getBytes("UTF-8");
			URL url = new URL(webAddress + "/do");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.getOutputStream().write(postDataBytes);
			String line;
			int code = conn.getResponseCode();
			if (code == 406) {
				throw new BusyBotException();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				log.debug("Response from Bot: {}", line);
			}
			reader.close();
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean hasPlan() {
		return lastPlan != null;
	}

	@Override
	public void abort() throws MalformedURLException, IOException {
		lastPlan = null;
		HttpURLConnection conn = (HttpURLConnection) new URL(webAddress + "/do").openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("DELETE");
		if (conn.getResponseCode() != 204) {
			throw new IOException(conn.getResponseMessage());
		}
	}

	@Override
	public String whatHaveYouDone() throws IOException {
		try {
			URLConnection connection = new URL(webAddress + "/do").openConnection();
			return IOUtils.toString(connection.getInputStream());
		} catch (IOException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String currentPlan() throws IOException {
		try {
			URLConnection connection = new URL(webAddress + "/currentplan").openConnection();
			return IOUtils.toString(connection.getInputStream());
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("[").append(webAddress.toString())
				.append("]").toString();
	}
}
