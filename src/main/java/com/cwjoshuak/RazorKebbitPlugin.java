package com.cwjoshuak;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.callback.ClientThread;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Razor Kebbit Tracking",
	description = "Track razor-backed kebbits like you would Herbiboar.",
	tags = {"razor", "kebbit", "backed", "razorback", "razorbacked", "razor-backed", "kebbits", "hunter", "rumour"}
)
@Getter
public class RazorKebbitPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RazorKebbitConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RazorKebbitOverlay overlay;


	private static final List<WorldPoint> END_LOCATIONS = ImmutableList.of(
		new WorldPoint(2358, 3620, 0),
		new WorldPoint(2351, 3619, 0),
		new WorldPoint(2362, 3615, 0),
		new WorldPoint(2354, 3609, 0),
		new WorldPoint(2357, 3607, 0),
		new WorldPoint(2349, 3604, 0),
		new WorldPoint(2360, 3602, 0),
		new WorldPoint(2355, 3601, 0)
	);

	private static final Set<Integer> START_OBJECT_IDS = ImmutableSet.of(
		ObjectID.BURROW,
		ObjectID.BURROW_19439,
		ObjectID.BURROW_19440
	);

	private static final Integer RAZOR_KEBBIT_REGION = 9272;
	private static final Integer VARBIT_FINISH = 2994;
	List<Integer> varbitIds = Arrays.stream(RBKebbitSearchSpot.values()).map(s -> s.varbit).collect(Collectors.toList());
	@Getter
	private final List<WorldPoint> currentPath = Lists.newArrayList();

	@Getter
	private final Map<WorldPoint, TileObject> trailObjects = new HashMap<>();

	@Getter
	private final Map<WorldPoint, TileObject> burrows = new HashMap<>();

	@Getter
	private final Map<WorldPoint, TileObject> bushes = new HashMap<>();

	private boolean inRazorKebbitArea;
	private int finishId;
	public static final String[] TRAIL_MENU_ENTRY_TARGETS = new String[]{
		"Plant", "Bush", "Burrow"
	};

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(overlay);

		if (client.getGameState() == GameState.LOGGED_IN) {
			clientThread.invokeLater(() ->
			{
				inRazorKebbitArea = checkArea();
				updateTrailData(null);
			});
		}
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
		resetTrailData();
		clearCache();
		inRazorKebbitArea = false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		switch (event.getGameState()) {
			case HOPPING:
			case LOGGING_IN:
				resetTrailData();
				break;
			case LOADING:
				clearCache();
				inRazorKebbitArea = checkArea();
				break;
			default:
				break;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event) {

		updateTrailData(event);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {

		onTileObject(null, event.getGameObject());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		onTileObject(event.getGameObject(), null);
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event) {

		onTileObject(null, event.getGroundObject());
	}

	@Subscribe
	public void onGroundObjectDespawned(GroundObjectDespawned event) {

		onTileObject(event.getGroundObject(), null);
	}

	private boolean checkArea() {
		final int[] mapRegions = client.getMapRegions();
		return ArrayUtils.contains(mapRegions, RAZOR_KEBBIT_REGION);
	}

	// Store relevant GameObjects
	private void onTileObject(TileObject oldObject, TileObject newObject) {

		if (oldObject != null) {
			WorldPoint oldLocation = oldObject.getWorldLocation();
			burrows.remove(oldLocation);
			trailObjects.remove(oldLocation);
			bushes.remove(oldLocation);
		}

		if (newObject == null) {
			return;
		}
		if (START_OBJECT_IDS.contains(newObject.getId())) {
			burrows.put(newObject.getWorldLocation(), newObject);
			return;
		}
		if (RBKebbitSearchSpot.isSearchSpot(newObject.getWorldLocation())) {
			trailObjects.put(newObject.getWorldLocation(), newObject);
			return;
		}
		if (END_LOCATIONS.contains(newObject.getWorldLocation())) {
			bushes.put(newObject.getWorldLocation(), newObject);
			return;
		}
	}


	private void updateTrailData(VarbitChanged event) {
		if (!inRazorKebbitArea || event == null) {
			return;
		}
		finishId = client.getVarbitValue(VARBIT_FINISH);
		if (varbitIds.contains(event.getVarbitId())) {
			WorldPoint wp = RBKebbitSearchSpot.worldPoint(event.getVarbitId());
			if (event.getValue() == 1 || event.getValue() == 2 || event.getValue() == 3) {
				if (currentPath.contains(wp)) {
					currentPath.remove(wp);
				} else {
					currentPath.add(wp);
				}
			} else {
				currentPath.remove(wp);
			}
		} else if (event.getVarbitId() == VARBIT_FINISH && event.getValue() == 0) {
			resetTrailData();
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event) {
		if (!isInRazorKebbitArea()) {
			return;
		}

		if (config.dynamicMenuEntrySwap()) {
			swapTrailMenuEntries(event);
		}
	}


	private void swapTrailMenuEntries(MenuEntryAdded event) {
		String target = event.getTarget();
		for (String menuTarget : TRAIL_MENU_ENTRY_TARGETS) {
			if (target.contains(menuTarget)) {
				MenuEntry entry = event.getMenuEntry();
				WorldPoint entryTargetPoint = WorldPoint.fromScene(client, entry.getParam0(), entry.getParam1(), client.getPlane());

				if (finishId == 0) {
					if (currentPath.isEmpty() && burrows.get(entryTargetPoint) == null) {
						entry.setDeprioritized(true);
					} else if (!currentPath.isEmpty() && !entryTargetPoint.equals(currentPath.get(currentPath.size() - 1))) {
						entry.setDeprioritized(true);
					}
				} else {
					if (!END_LOCATIONS.contains(entryTargetPoint)) {
						entry.setDeprioritized(true);
					} else {
						if (!entry.getOption().equals("Attack")) {
							entry.setDeprioritized(true);
						}
					}
				}

				return;
			}
		}
	}


	@Provides
	RazorKebbitConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(RazorKebbitConfig.class);
	}

	private void resetTrailData() {
		finishId = 0;
		currentPath.clear();
	}

	private void clearCache() {
		burrows.clear();
		trailObjects.clear();
		bushes.clear();
	}

	List<WorldPoint> getEndLocations() {
		return END_LOCATIONS;
	}
}
