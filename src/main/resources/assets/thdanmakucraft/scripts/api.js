const thdanmakucraft = {};
const minecraft = {}

minecraft.Vec3 = Java.type("net.minecraft.world.phys.Vec3");
minecraft.Vec2 = Java.type("net.minecraft.world.phys.Vec2");
minecraft.Mth = Java.type("net.minecraft.util.Mth");
minecraft.AABB = Java.type("net.minecraft.util.AABB")

thdanmakucraft.THObject = Java.type("com.asrian.thDanmakuCraft.world.entity.danmaku.THObject");
thdanmakucraft.THBullet = Java.type("com.asrian.thDanmakuCraft.world.entity.danmaku.THBullet");
thdanmakucraft.THLaser = Java.type("com.asrian.thDanmakuCraft.world.entity.danmaku.THLaser");
thdanmakucraft.ThCurvedLaser = Java.type("com.asrian.thDanmakuCraft.world.entity.danmaku.THCurvedLaser");

Java = null;