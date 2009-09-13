/**
 * 
 * Interfaces and classes to represent a programming contest.
 * 
 * <p>A state of a contest is represented by {@link se.kth.livetech.contest.Contest}.
 * Changes are represented by {@link se.kth.livetech.contest.Attrs}.
 * A timestamped change is an {@link se.kth.livetech.contest.AttrsUpdateEvent},
 * observed by {@link se.kth.livetech.contest.AttrsUpdateListener}.
 * A contest update is a {@link se.kth.livetech.contest.ContestUpdateEvent},
 * observed by {@link se.kth.livetech.contest.ContestUpdateListener}.
 * </p>
 * 
 * <p>The implementation {@link se.kth.livetech.contest.impl.ContestImpl}
 * has a contructor to form a new contest state from an old one
 * and an {@link se.kth.livetech.contest.Attrs} update. Relevant maps
 * are copied so that the old state remains valid.</p>
 * 
 * <p>Ranking can be obtained from {@link se.kth.livetech.contest.Contest},
 * {@link se.kth.livetech.contest.TeamScore} and
 * {@link se.kth.livetech.contest.ProblemScore}.</p>
 * 
 * <p>{@link se.kth.livetech.contest.impl.LogListener} writes a log file, which
 * {@link se.kth.livetech.contest.impl.LogSpeaker} reads.</p>
 * 
 * <p>{@link se.kth.livetech.contest.impl.ContestPlayer}s replays
 * {@link se.kth.livetech.contest.AttrsUpdateEvent}s.
 * {@link se.kth.livetech.contest.impl.ContestReplay} in order and
 * {@link se.kth.livetech.contest.impl.ContestReorder} reorders problem submissions.
 * {@link se.kth.livetech.contest.impl.TimeDelay}
 * replays contest updates with a delay.
 * </p>
 * 
 * <p>Attrs hierarchy:
 * <ul>
 *  <li>{@link se.kth.livetech.contest.Attrs}
 *  <ul>
 *   <li>{@link se.kth.livetech.contest.Info} - general contest info</li>
 *   <li>{@link se.kth.livetech.contest.Entity} - things with a name and id</li>
 *   <ul>
 *    <li>{@link se.kth.livetech.contest.Judgement}</li>
 *    <li>{@link se.kth.livetech.contest.Language}</li>
 *    <li>{@link se.kth.livetech.contest.Problem}</li>
 *    <li>{@link se.kth.livetech.contest.Team}</li>
 *   </ul></li>
 *   <li>{@link se.kth.livetech.contest.Sub} - submissions</li>
 *   <ul>
 *    <li>{@link se.kth.livetech.contest.Run}</li>
 *    <li>{@link se.kth.livetech.contest.Clar}</li>
 *   </ul></li>
 *  </ul></li>
 * </ul></p>
 */
package se.kth.livetech.contest.impl;
