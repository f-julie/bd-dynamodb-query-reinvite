package com.amazon.ata.dynamodbquery.activity;

import com.amazon.ata.dynamodbquery.dao.EventAnnouncementDao;
import com.amazon.ata.dynamodbquery.dao.models.EventAnnouncement;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetEventAnnouncementsActivity {
    private EventAnnouncementDao eventAnnouncementDao;

    /**
     * Constructs an Activity with the given DAO.
     * @param eventAnnouncementDao The EventDao to use for fetching event
     */
    @Inject
    public GetEventAnnouncementsActivity(EventAnnouncementDao eventAnnouncementDao) {
        this.eventAnnouncementDao = eventAnnouncementDao;
    }

    /**
     * Gets event announcements between the given startTime and endTime (inclusive).
     *
     * NOTE: A little deviation from usual.
     * Here we're using values directly in our arguments and return value,
     * whereas in a typical Coral service we'd have Request/Result objects
     * that would be generated from configuration via Coral. We haven't
     * created service infrastructure for this activity, so we're just
     * using the values directly.
     *
     * @param eventId The ID of the event to get
     * @return List of announcements for the event.
     */
    public List<EventAnnouncement> handleRequest(final String eventId) {
        // TODO: implement
        return eventAnnouncementDao.getEventAnnouncements(eventId);
    }
}
