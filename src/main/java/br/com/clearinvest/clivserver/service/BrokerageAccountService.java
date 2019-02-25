package br.com.clearinvest.clivserver.service;

import br.com.clearinvest.clivserver.domain.BrokerageAccount;
import br.com.clearinvest.clivserver.repository.BrokerageAccountRepository;
import br.com.clearinvest.clivserver.repository.UserRepository;
import br.com.clearinvest.clivserver.security.SecurityUtils;
import br.com.clearinvest.clivserver.service.dto.BrokerageAccountDTO;
import br.com.clearinvest.clivserver.service.mapper.BrokerageAccountMapper;
import br.com.clearinvest.clivserver.web.rest.errors.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing BrokerageAccount.
 */
@Service
@Transactional
public class BrokerageAccountService {

    private final Logger log = LoggerFactory.getLogger(BrokerageAccountService.class);

    private final BrokerageAccountRepository brokerageAccountRepository;

    private final BrokerageAccountMapper brokerageAccountMapper;

    private final UserRepository userRepository;

    public BrokerageAccountService(BrokerageAccountRepository brokerageAccountRepository,
        BrokerageAccountMapper brokerageAccountMapper, UserRepository userRepository) {
        this.brokerageAccountRepository = brokerageAccountRepository;
        this.brokerageAccountMapper = brokerageAccountMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a brokerageAccount.
     *
     * @param brokerageAccountDTO the entity to save
     * @return the persisted entity
     */
    public BrokerageAccountDTO save(BrokerageAccountDTO brokerageAccountDTO) {
        log.debug("Request to save BrokerageAccount : {}", brokerageAccountDTO);

        BrokerageAccount brokerageAccount = brokerageAccountMapper.toEntity(brokerageAccountDTO);
        brokerageAccount = brokerageAccountRepository.save(brokerageAccount);
        return brokerageAccountMapper.toDto(brokerageAccount);
    }

    /**
     * Save a brokerageAccount.
     *
     * @param accountDTO the entity to save
     * @return the persisted entity
     */
    public BrokerageAccountDTO saveWithCurrentUser(BrokerageAccountDTO accountDTO) {
        log.debug("Request to save BrokerageAccount : {}", accountDTO);

        if (accountDTO.getId() == null) {
            Optional<BrokerageAccount> accountOptional = brokerageAccountRepository
                .findByBrokerageIdAndCurrentUser(accountDTO.getBrokerageId());
            if (accountOptional.isPresent()) {
                throw new BusinessException("Já existe uma conta cadastrada para essa corretora.");

            } else {
                // TODO validar conta (se email for obrigatório para corretora, verificar se email veio, etc)

                BrokerageAccount account = brokerageAccountMapper.toEntity(accountDTO);

                SecurityUtils.getCurrentUserLogin()
                    .flatMap(userRepository::findOneByLogin)
                    .ifPresent(account::setUser);

                account = brokerageAccountRepository.save(account);
                return brokerageAccountMapper.toDto(account);
            }

        } else {
            Optional<BrokerageAccount> accountOptional = brokerageAccountRepository
                .findByIdAndCurrentUser(accountDTO.getId());

            if (accountOptional.isPresent()) {
                // TODO validar conta (se email for obrigatório para corretora, verificar se email veio, etc)

                BrokerageAccount account = accountOptional.get();

                SecurityUtils.getCurrentUserLogin()
                    .flatMap(userRepository::findOneByLogin)
                    .ifPresent(account::setUser);

                account.setLoginAccessCode(accountDTO.getLoginAccessCode());
                account.setLoginCpf(accountDTO.getLoginCpf());
                account.setLoginEmail(accountDTO.getLoginEmail());
                account.setLoginPassword(accountDTO.getLoginPassword());

                account = brokerageAccountRepository.save(account);
                return brokerageAccountMapper.toDto(account);

            } else {
                throw new BusinessException("Conta não encontrada.");
            }
        }
    }

    /**
     * Get all the brokerageAccounts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BrokerageAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BrokerageAccounts");
        return brokerageAccountRepository.findAll(pageable)
            .map(brokerageAccountMapper::toDto);
    }


    /**
     * Get one brokerageAccount by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<BrokerageAccountDTO> findOne(Long id) {
        log.debug("Request to get BrokerageAccount : {}", id);
        return brokerageAccountRepository.findById(id)
            .map(brokerageAccountMapper::toDto);
    }

    /**
     * Delete the brokerageAccount by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete BrokerageAccount : {}", id);
        brokerageAccountRepository.deleteById(id);
    }
}
