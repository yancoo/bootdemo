package hh.bootdemo.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

	// http://localhost:8080/session?page=0&size=2&sort=requestCount,desc&sort=username,asc
	@GetMapping("session")
	public Page<SessionDTO> findSessions(Pageable pageable) {
		List<SessionDTO> sessions = new ArrayList<SessionDTO>();
		for (Session session : SessionCache.getSessions().values()) {
			sessions.add(session.createDTO());
		}

		Sort sort = pageable.getSort();
		if (sort != null) {
			Iterator<Order> iter = sort.iterator();
			while (iter.hasNext()) {
				Order order = (Order) iter.next();
				String propertyName = order.getProperty();
				Collections.sort(sessions,
						new SimplePropertyComparator<SessionDTO>(propertyName, order.getDirection()));
			}
		}

		List<SessionDTO> data = new ArrayList<SessionDTO>();
		int totalCount = sessions.size();
		if (totalCount > 0) {
			int start = pageable.getPageNumber() * pageable.getPageSize();
			if (start < totalCount) {
				int end = start + pageable.getPageSize() - 1;
				if (end >= totalCount) {
					end = totalCount - 1;
				}
				for (int i = start; i <= end; i++) {
					data.add(sessions.get(i));
				}
			}
		}
		return new PageImpl<SessionDTO>(data, pageable, SessionCache.getSessions().size());
	}
}
