package hh.bootdemo.journal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hh.bootdemo.utils.RequestUtils;

@Service
public class JournalService {

	@Autowired
	JournalRepository journalRepository;

	/**
	 * save journal
	 * 
	 * @param journal
	 */
	public void save(Journal journal) {
		journalRepository.save(journal);
	}

	/**
	 * 用户操作日志
	 *
	 * @param menu
	 *            菜单
	 * @param subMenu
	 *            子tab菜单，可空
	 * @param type
	 *            获得操作日志类型JournalType
	 * @param objType
	 *            操作对象类型，如user、server等
	 * @param content
	 *            内容
	 */
	public void saveJournal(String menu, String subMenu, String type, String objType, String content) {
		Map<String, String> remoteMap = RequestUtils.getRemoteMap();
		Journal journal = new Journal(menu, subMenu, type, objType, remoteMap.get("username"), remoteMap.get("ip"),
				remoteMap.get("sesionId"), content);
		journalRepository.save(journal);
		JournalThreadPoolExecutor.getInstance().execute(journal);// 可通知外部处理journal
	}
}
