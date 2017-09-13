package com.oy.httpasyncclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class HttpclientTest {
	public static void main(String[] argv) {
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		httpclient.start();

		final CountDownLatch latch = new CountDownLatch(1);
		final HttpGet request = new HttpGet("http://192.168.1.152:8082/mall/mallInterface_test111.action");

		System.out.println(" caller thread id is : " + Thread.currentThread().getId());

		//并不是真正的异步
		httpclient.execute(request, new FutureCallback<HttpResponse>() {

			public void completed(final HttpResponse response) {
				latch.countDown();
				System.out.println(" callback thread id is : " + Thread.currentThread().getId());
				System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
				try {
					String content = EntityUtils.toString(response.getEntity(), "UTF-8");
					System.out.println(" response content is : " + content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void failed(final Exception ex) {
				latch.countDown();
				System.out.println(request.getRequestLine() + "->" + ex);
				System.out.println(" callback thread id is : " + Thread.currentThread().getId());
			}

			public void cancelled() {
				latch.countDown();
				System.out.println(request.getRequestLine() + " cancelled");
				System.out.println(" callback thread id is : " + Thread.currentThread().getId());
			}

		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			httpclient.close();
		} catch (IOException ignore) {

		}
	}
}