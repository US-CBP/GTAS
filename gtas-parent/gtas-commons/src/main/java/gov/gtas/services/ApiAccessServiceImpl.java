/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;


import gov.gtas.model.ApiAccess;
import gov.gtas.repository.ApiAccessRepository;
import gov.gtas.vo.ApiAccessVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiAccessServiceImpl implements ApiAccessService {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Resource
    private ApiAccessRepository apiAccessRepository;

    @Override
    @Transactional
    public ApiAccessVo create(ApiAccessVo apiAccessVo) {
        apiAccessVo.setPassword(passwordEncoder.encode(apiAccessVo
                .getPassword()));

        ApiAccess savedApiAccess = apiAccessRepository.save(buildApiAccess(apiAccessVo));

        return buildApiAccessVo(savedApiAccess);
    }

    @Override
    @Transactional
    public ApiAccessVo delete(Long id) {
        ApiAccessVo apiAccessVo = this.findById(id);

        if (apiAccessVo != null) {
            apiAccessRepository.delete(buildApiAccess(apiAccessVo));
        }

        return apiAccessVo;
    }

    @Override
    @Transactional
    public List<ApiAccessVo> findAll() {
        List<ApiAccess> allApiAccesses = (List<ApiAccess>) apiAccessRepository.findAll();

        List<ApiAccessVo> allApiAccessVos = new ArrayList<>();

        for (ApiAccess apiAccess : allApiAccesses) {
            allApiAccessVos.add(buildApiAccessVo(apiAccess));
        }

        return allApiAccessVos;
    }

    @Override
    @Transactional
    public ApiAccessVo update(ApiAccessVo apiAccessVo) {
        //If the password changed we need to encrypt it
        if (!passwordEncoder.matches(findById(apiAccessVo.getId()).getPassword(), apiAccessVo.getPassword())) {
            apiAccessVo.setPassword(passwordEncoder.encode(apiAccessVo
                    .getPassword()));
        }

        ApiAccess updatedApiAccess = apiAccessRepository.save(buildApiAccess(apiAccessVo));

        return buildApiAccessVo(updatedApiAccess);
    }

    @Override
    @Transactional
    public ApiAccessVo findById(Long id) {
        ApiAccess apiAccess = apiAccessRepository.findById(id).orElse(null);

        if (apiAccess == null) {
            return null;
        }

        return buildApiAccessVo(apiAccess);
    }

    static ApiAccessVo buildApiAccessVo(ApiAccess apiAccess) {
        ApiAccessVo apiAccessVo = new ApiAccessVo(apiAccess.getUsername(), apiAccess.getPassword(), apiAccess.getEmail(), apiAccess.getOrganization());

        if (apiAccess.getId() != null) {
            apiAccessVo.setId(apiAccess.getId());
        }

        if (apiAccess.getCreatedAt() != null) {
            apiAccessVo.setCreatedAt(new java.util.Date(apiAccess.getCreatedAt().getTime()));
        }

        if (apiAccess.getUpdatedAt() != null) {
            apiAccessVo.setUpdatedAt(new java.util.Date(apiAccess.getUpdatedAt().getTime()));
        }

        apiAccessVo.setCreatedBy(apiAccess.getCreatedBy());
        apiAccessVo.setUpdatedBy(apiAccess.getUpdatedBy());

        return apiAccessVo;
    }

    static ApiAccess buildApiAccess(ApiAccessVo apiAccessVo) {
        ApiAccess apiAccess = new ApiAccess(apiAccessVo.getUsername(), apiAccessVo.getPassword(), apiAccessVo.getEmail(), apiAccessVo.getOrganization());

        if (apiAccessVo.getId() != null) {
            apiAccess.setId(apiAccessVo.getId());
        }

        if (apiAccessVo.getCreatedAt() != null) {
            apiAccess.setCreatedAt(new java.sql.Date(apiAccessVo.getCreatedAt().getTime()));
        }

        if (apiAccessVo.getUpdatedAt() != null) {
            apiAccess.setUpdatedAt(new java.sql.Date(apiAccessVo.getUpdatedAt().getTime()));
        }

        apiAccess.setCreatedBy(apiAccessVo.getCreatedBy());
        apiAccess.setUpdatedBy(apiAccessVo.getUpdatedBy());

        return apiAccess;
    }

}
