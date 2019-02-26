package gov.gtas.services;

import gov.gtas.model.Address;
import gov.gtas.model.FlightPax;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.vo.passenger.AddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApisControllerServiceImpl implements ApisControllerService {


    @Autowired
    ApisMessageRepository apisMessageRepository;

    @Transactional
    public List<FlightPassengerVo> generateFlightPaxVoByApisRef(String ref) {
        List<FlightPax> fpList = apisMessageRepository.findFlightPaxByApisRef(ref);
        List<FlightPassengerVo> flightPassengerVos = new ArrayList<>();
        for (FlightPax fp : fpList) {
            FlightPassengerVo fpVo = new FlightPassengerVo();
            fpVo.setFirstName(fp.getPassenger().getFirstName());
            fpVo.setLastName(fp.getPassenger().getLastName());
            fpVo.setMiddleName(fp.getPassenger().getMiddleName());
            fpVo.setEmbarkation(fp.getEmbarkation());
            fpVo.setDebarkation(fp.getDebarkation());
            if (fp.getInstallationAddress() != null) {
                AddressVo add = new AddressVo();
                Address installAdd = fp.getInstallationAddress();
                add.setLine1(installAdd.getLine1());
                add.setLine2(installAdd.getLine2());
                add.setLine3(installAdd.getLine3());
                add.setCity(installAdd.getCity());
                add.setCountry(installAdd.getCountry());
                add.setPostalCode(installAdd.getPostalCode());
                add.setState(installAdd.getState());
                fpVo.setInstallationAddress(add);
            }
            fpVo.setPortOfFirstArrival(fp.getPortOfFirstArrival());
            fpVo.setResidencyCountry(fp.getResidenceCountry());
            fpVo.setPassengerType(fp.getTravelerType());
            fpVo.setCitizenshipCountry(fp.getPassenger().getCitizenshipCountry());
            fpVo.setResRefNumber(fp.getReservationReferenceNumber());
            fpVo.setFlightId(fp.getFlight().getId());
            fpVo.setPassengerId(fp.getPassenger().getId());
            flightPassengerVos.add(fpVo);
        }
        return flightPassengerVos;
    }
}
