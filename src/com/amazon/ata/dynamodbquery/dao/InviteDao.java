package com.amazon.ata.dynamodbquery.dao;

import com.amazon.ata.dynamodbquery.dao.models.Invite;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import javax.inject.Inject;

/**
 * Manages access to Invite items.
 */
public class InviteDao {
    private DynamoDBMapper mapper;

    /**
     * Constructs a DAO with the given mapper.
     * @param mapper The DynamoDBMapper to use
     */
    @Inject
    public InviteDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Fetches an invite by event ID and member ID.
     * @param eventId The event ID of the invite
     * @param memberId The member ID of the invite
     * @return the invite, if found; null otherwise
     */
    public Invite getInvite(String eventId, String memberId) {
        return mapper.load(Invite.class, eventId, memberId);
    }

    /**
     * Fetches all invites sent to a given member.
     * @param memberId The ID of the member to fetch invites for (sent to)
     * @return List of Invite objects sent to the given member
     */
    public List<Invite> getInvitesSentToMember(String memberId) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("memberId = :memberId")
            .withExpressionAttributeValues(ImmutableMap.of(":memberId", new AttributeValue(memberId)));
        return new ArrayList<>(mapper.scan(Invite.class, scanExpression));
    }

    /**
     * Fetches all *accepted* invites for a given event ID.
     * @param eventId The ID of the event to query invites for.
     * @return List of accepted Invite objects for the given ID
     */
    public List<Invite> getAcceptedInvitesForEvent(String eventId) {
        return Collections.emptyList();
    }

    /**
     * Fetches a page of invites (with 10 per page) for a given event ID and exclusiveStartInviteId.
     *
     * @param eventId The ID of the event to query invites for.
     * @param exclusiveStartMemberId The exclusiveStartMemberId which corresponds to the last invite returned from the
     *                               previous page. We will return the next set of invites following this id.
     * @return Paginated list of invites.
     */
    public List<Invite> getInvitesForEvent(String eventId, String exclusiveStartMemberId) {
        // TODO: implement
        Invite invite = new Invite();
        invite.setEventId(eventId);

        Map<String, AttributeValue> exclusiveStartKey = null;
        if (exclusiveStartMemberId != null) {
            exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("eventId", new AttributeValue().withS(eventId));
            exclusiveStartKey.put("memberId", new AttributeValue().withS(exclusiveStartMemberId));
        }

        DynamoDBQueryExpression<Invite> queryExpression =
                new DynamoDBQueryExpression<Invite>()
                .withHashKeyValues(invite)
                .withLimit(10)
                .withExclusiveStartKey(exclusiveStartKey);

        QueryResultPage<Invite> queryResultPage =
            mapper.queryPage(Invite.class, queryExpression);

        return queryResultPage.getResults();
    }

    /**
     * Creates a new invite.
     * @param invite The invite to create
     * @return The newly created invite
     */
    public Invite createInvite(Invite invite) {
        mapper.save(invite);
        return invite;
    }

    /**
     * Cancels the invite corresponding to the event + member IDs.
     * @param eventId event ID for the invite to cancel
     * @param memberId member ID for the invite to cancel
     * @return The updated Invite if found; null otherwise.
     */
    public Invite cancelInvite(String eventId, String memberId) {
        Invite invite = mapper.load(Invite.class, eventId, memberId);
        if (Objects.isNull(invite)) {
            return null;
        }

        if (!invite.isCanceled()) {
            invite.setCanceled(true);
            mapper.save(invite);
        }
        return invite;
    }

    /**
     * Deletes the invite indicated by eventId, memberId.
     * For extra safety, deletes conditional on the invite not having been
     * accepted (isAttending is false).
     * @param eventId The event the invite is for
     * @param memberId The member the invite is sent to
     * @return true if the invite was deleted; false if it was not deleted because the
     *         invite isAttending is set to true.
     */
    public boolean deleteInvite(String eventId, String memberId) {
        Invite inviteToDelete = new Invite();
        inviteToDelete.setEventId(eventId);
        inviteToDelete.setMemberId(memberId);

        DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression();
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue()
            .withComparisonOperator(ComparisonOperator.NE)
            .withValue(new AttributeValue().withBOOL(true));
        deleteExpression.withExpectedEntry("isAttending", expectedAttributeValue);

        try {
            mapper.delete(inviteToDelete, deleteExpression);
        } catch (ConditionalCheckFailedException e) {
            // check failed, delete didn't happen
            return false;
        }

        return true;
    }
}
