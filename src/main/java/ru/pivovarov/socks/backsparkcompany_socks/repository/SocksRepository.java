package ru.pivovarov.socks.backsparkcompany_socks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.pivovarov.socks.backsparkcompany_socks.model.Sock;

@Repository
public interface SocksRepository extends JpaRepository<Sock, Long>, JpaSpecificationExecutor<Sock> {
}
