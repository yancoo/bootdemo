package hh.bootdemo.journal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "journal", path = "journal")
public interface JournalRepository extends PagingAndSortingRepository<Journal, Long> {

	Page<Journal> findByUserName(@Param(value = "userName") String userName, Pageable pageable);
}