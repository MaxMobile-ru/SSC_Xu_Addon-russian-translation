package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;
import xu_mod.SSCXuAddon.SSCXuAddon;

import java.util.function.Consumer;

public class WebBridgeAction {
    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("web_bridge"),
                new SerializableData()
                        .add("web_bridge_length", SerializableDataTypes.INT, 16)
                        .add("web_bridge_width", SerializableDataTypes.INT, 0),
                (data, entity) -> {
                    BlockPos pos = entity.getBlockPos().down();
                    Direction direction = entity.getHorizontalFacing();
                    net.onixary.shapeShifterCurseFabric.additional_power.WebBridgeAction.BuildWebBridge(entity.getWorld(), pos, direction, new net.onixary.shapeShifterCurseFabric.additional_power.WebBridgeAction.WebBridgeConfig(data.getInt("web_bridge_length"), data.getInt("web_bridge_width")), RegCustomBlock.TEMP_WEB_BRIDGE);
                }
        ));
    }
}
