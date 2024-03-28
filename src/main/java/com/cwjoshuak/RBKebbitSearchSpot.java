package com.cwjoshuak;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.Set;

public enum RBKebbitSearchSpot {
	VB_2974(ObjectID.PLANT_19356, new WorldPoint(2362, 3598, 0), 2974),
	VB_2975(ObjectID.PLANT_19357, new WorldPoint(2355, 3598, 0), 2975),
	VB_2976(ObjectID.PLANT_19358, new WorldPoint(2347, 3603, 0), 2976),
	VB_2977(ObjectID.PLANT_19359, new WorldPoint(2358, 3599, 0), 2977),
	VB_2978(ObjectID.PLANT_19360, new WorldPoint(2352, 3603, 0), 2978),
	VB_2979(ObjectID.PLANT_19361, new WorldPoint(2358, 3603, 0), 2979),
	VB_2980(ObjectID.PLANT_19362, new WorldPoint(2363, 3602, 0), 2980),

	VB_2981(ObjectID.PLANT_19363, new WorldPoint(2358, 3607, 0), 2981),
	VB_2982(ObjectID.PLANT_19364, new WorldPoint(2355, 3608, 0), 2982),
	VB_2983(ObjectID.PLANT_19365, new WorldPoint(2351, 3608, 0), 2983),
	VB_2985(ObjectID.PLANT_19372, new WorldPoint(2363, 3617, 0), 2985),
	VB_2986(ObjectID.PLANT_19375, new WorldPoint(2349, 3620, 0), 2986),
	VB_2987(ObjectID.PLANT_19374, new WorldPoint(2356, 3620, 0), 2987),
	VB_2988(ObjectID.PLANT_19375, new WorldPoint(2344, 3612, 0), 2988),
	VB_2989(ObjectID.PLANT_19376, new WorldPoint(2352, 3612, 0), 2989),
	VB_2990(ObjectID.PLANT_19377, new WorldPoint(2349, 3617, 0), 2990),
	VB_2991(ObjectID.PLANT_19378, new WorldPoint(2352, 3618, 0), 2991),
	VB_2992(ObjectID.PLANT_19379, new WorldPoint(2362, 3614, 0), 2992),
	VB_2993(ObjectID.PLANT_19379, new WorldPoint(2360, 3618, 0), 2993);


	int objectId;
	WorldPoint wp;
	int varbit;

	private static final Set<WorldPoint> SPOTS;

	RBKebbitSearchSpot(int objectId, WorldPoint worldPoint, int varbit) {
		this.objectId = objectId;
		this.wp = worldPoint;
		this.varbit = varbit;
	}

	static {
		ImmutableSet.Builder<WorldPoint> spotBuilder = new ImmutableSet.Builder<>();
		for (RBKebbitSearchSpot spot : values()) {
			spotBuilder.add(spot.wp);
		}
		SPOTS = spotBuilder.build();
	}

	static boolean isSearchSpot(WorldPoint location) {
		return SPOTS.contains(location);
	}

	static WorldPoint worldPoint(int varbit) {
		return Arrays.stream(values()).filter(s -> s.varbit == varbit).findFirst().get().wp;
	}
}
