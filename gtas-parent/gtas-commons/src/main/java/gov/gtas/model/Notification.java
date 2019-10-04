/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import gov.gtas.enumtype.NotificationSend;
import gov.gtas.enumtype.NotificationStatus;
import gov.gtas.enumtype.NotificationType;

import javax.persistence.*;

@Entity
@Table(name = "notification")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Notification extends BaseEntityAudit {

    @Column(name = "n_passenger", columnDefinition = "bigint unsigned", nullable = false)
    private Passenger passenger;

    @Enumerated(EnumType.STRING)
    @Column(name = "n_type", nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "n_status", nullable = false)
    private NotificationStatus notificationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_user", referencedColumnName = "user_id", nullable = false)
    private User notificationOwner;

    @Enumerated(EnumType.STRING)
    @Column(name = "n_send_when", nullable = false)
    private NotificationSend notificationSend;

}
