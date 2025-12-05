package utb.fai.soapservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import utb.fai.soapservice.Model.AuthorPersistent;

public interface AuthorRepository extends JpaRepository<AuthorPersistent, Long> {

}
