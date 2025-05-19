package vn.iotstar.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Query("SELECT r FROM roles r WHERE r.name = :role")
    Role findByName(@Param("role") String role);
}
