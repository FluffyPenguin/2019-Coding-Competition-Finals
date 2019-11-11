package com.statefarm.codingcomp.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.statefarm.codingcomp.model.Policy;

public class Reader {

	public List<Policy> read() throws Exception {
		URL url = this.getClass().getResource("/policies.csv");
		URI uri = new URI(url.toString());
		File file = new File(uri.getPath());
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		try {
			fis.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		String[] contents = new String(data, "UTF-8").split("\\r?\\n");
		
		List<Policy> policies = new ArrayList<>();
		for(String policy : contents) {
			policies.add(new Policy(policy.split(",")));
		}
		return policies;
	}
}
