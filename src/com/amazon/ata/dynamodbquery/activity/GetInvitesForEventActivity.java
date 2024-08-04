package com.amazon.ata.dynamodbquery.activity;

import com.amazon.ata.dynamodbquery.dao.InviteDao;
import com.amazon.ata.dynamodbquery.dao.models.Invite;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

/**
 * Handles requests to get a paginated list of invites for a given event.
 */
public class GetInvitesForEventActivity {
    private InviteDao inviteDao;

    /**
     * Constructs the Activity with the given DAO object.
     * @param inviteDao The InviteDao to use for querying invites
     */
    @Inject
    public GetInvitesForEventActivity(InviteDao inviteDao) {
        this.inviteDao = inviteDao;
    }

    /**
     * Fetches a page of invites for a given event. If none are found, returns empty list.
     *
     * @param eventId The ID of the event to query invites for
     * @param exclusiveStartMemberId The member ID of the last Invite returned from the previous page.
     * @return Paginated list of Invite objects for the event.
     */
    public List<Invite> handleRequest(final String eventId, final String exclusiveStartMemberId) {
        // TODO: implement
        return inviteDao.getInvitesForEvent(eventId, exclusiveStartMemberId);
    }
}