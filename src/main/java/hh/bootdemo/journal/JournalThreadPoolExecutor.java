/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.journal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JournalThreadPoolExecutor
 * 
 * @author yan
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JournalThreadPoolExecutor {

	protected final Log log = LogFactory.getLog(getClass());

	private static JournalThreadPoolExecutor instance;

	public static JournalThreadPoolExecutor getInstance() {
		if (instance == null) {
			instance = new JournalThreadPoolExecutor();
		}
		return instance;
	}

	private ThreadPoolExecutor executor;
	private List<IJournalHandler> journalHandlers;

	public JournalThreadPoolExecutor() {
		executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		journalHandlers = new ArrayList<IJournalHandler>();
	}

	public void registerHandler(Class clazz) {
		for (IJournalHandler handler : journalHandlers) {
			if (handler.getClass().getName().equals(clazz.getName())) {
				return;
			}
		}
		try {
			IJournalHandler handler = (IJournalHandler) clazz.newInstance();
			journalHandlers.add(handler);
		} catch (Exception e) {
			log.error("register error, " + clazz, e);
		}
	}

	protected void registerHandler(IJournalHandler handler) {
		if (!journalHandlers.contains(handler)) {
			journalHandlers.add(handler);
		}
	}

	public void execute(Journal journal) {
		executor.execute(new WorkRunable(journal));
	}

	private class WorkRunable implements Runnable {

		Journal journal;

		public WorkRunable(Journal journal) {
			this.journal = journal;
		}

		public void run() {
			for (IJournalHandler journalHandler : journalHandlers) {
				try {
					journalHandler.handle(journal);
				} catch (Exception e) {
					log.error("handler journal error, " + journal, e);
				}
			}
		}
	}

	public void shutdown() {
		executor.shutdown();
	}
}
