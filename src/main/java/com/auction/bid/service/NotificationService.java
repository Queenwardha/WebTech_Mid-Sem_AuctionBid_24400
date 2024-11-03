package com.auction.bid.service;

import com.auction.bid.model.Bidder;
import com.auction.bid.model.Notification;
import com.auction.bid.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(String message, Bidder bidder) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setBidder(bidder);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotifications(Long bidderId) {
        return notificationRepository.findByBidderId(bidderId);
    }
}

