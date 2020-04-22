// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package api.player.render.model;

import api.player.render.RenderPlayerAPI;
import api.player.render.asm.interfaces.IPlayerModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

public final class PlayerModelAPI<T extends LivingEntity>
{
    private final static Class<?>[] Class = new Class[]{PlayerModelAPI.class};
    private final static Class<?>[] Classes = new Class[]{PlayerModelAPI.class, String.class};
    private static boolean isCreated;
    private static final Logger logger = Logger.getLogger("PlayerModelAPI");
    protected final IPlayerModel<T> iPlayerModel;
    private final float textureOffsetX;
    private final float textureOffsetY;
    private final int textureWidth;
    private final int textureHeight;
    private final boolean smallArms;
    private final static Set<String> keys = new HashSet<>();
    private final static Map<String, String> keysToVirtualIds = new HashMap<>();
    private final static Set<Class<?>> dynamicTypes = new HashSet<>();
    private final static Map<Class<?>, Map<String, Method>> virtualDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> beforeDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> overrideDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> afterDynamicHookMethods = new HashMap<>();
    private final static List<String> beforeLocalConstructingHookTypes = new LinkedList<>();
    private final static List<String> afterLocalConstructingHookTypes = new LinkedList<>();
    private static final Map<String, List<String>> beforeDynamicHookTypes = new Hashtable<>(0);
    private static final Map<String, List<String>> overrideDynamicHookTypes = new Hashtable<>(0);
    private static final Map<String, List<String>> afterDynamicHookTypes = new Hashtable<>(0);
    private PlayerModelBase<T>[] beforeLocalConstructingHooks;
    private PlayerModelBase<T>[] afterLocalConstructingHooks;
    private final Map<PlayerModelBase<T>, String> baseObjectsToId = new Hashtable<>();
    private final Map<String, PlayerModelBase<T>> allBaseObjects = new Hashtable<>();
    private final Set<String> unmodifiableAllBaseIds = Collections.unmodifiableSet(this.allBaseObjects.keySet());
    private static final Map<String, Constructor<?>> allBaseConstructors = new Hashtable<>();
    private static final Set<String> unmodifiableAllIds = Collections.unmodifiableSet(allBaseConstructors.keySet());
    private static final Map<String, String[]> allBaseBeforeLocalConstructingSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeLocalConstructingInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterLocalConstructingSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterLocalConstructingInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseAfterDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseAfterDynamicInferiors = new Hashtable<>(0);
    private static boolean initialized = false;

    private static void log(String text)
    {
        System.out.println(text);
        logger.fine(text);
    }

    public static void register(String id, Class<?> baseClass)
    {
        register(id, baseClass, null);
    }

    public static void register(String id, Class<?> baseClass, PlayerModelBaseSorting baseSorting)
    {
        try {
            register(baseClass, id, baseSorting);
        } catch (RuntimeException exception) {
            if (id != null) {
                log("Model Player: failed to register id '" + id + "'");
            } else {
                log("Model Player: failed to register ModelPlayerBase");
            }

            throw exception;
        }
    }

    private static void register(Class<?> baseClass, String id, PlayerModelBaseSorting baseSorting)
    {
        if (!isCreated) {
            try {
                Method mandatory = PlayerModel.class.getMethod("getPlayerModelBase", String.class);
                if (mandatory.getReturnType() != PlayerModelBase.class) {
                    throw new NoSuchMethodException(PlayerModelBase.class.getName() + " " + PlayerModel.class.getName() + ".getPlayerModelBase(" + String.class.getName() + ")");
                }
            } catch (NoSuchMethodException exception) {
                String[] errorMessageParts = new String[]
                        {
                                "========================================",
                                "The API \"Model Player\" version " + RenderPlayerAPI.VERSION + " of the mod \"Render Player API " + RenderPlayerAPI.VERSION + "\" cannot be created!",
                                "----------------------------------------",
                                "Mandatory member method \"{0} getPlayerModelBase({3})\" not found in class \"{1}\".",
                                "There are three scenarios this can happen:",
                                "* Minecraft Forge is missing a Render Player API which Minecraft version matches its own.",
                                "  Download and install the latest Render Player API for the Minecraft version you were trying to run.",
                                "* The code of the class \"{2}\" of Render Player API has been modified beyond recognition by another Minecraft Forge mod.",
                                "  Try temporary uninstallation of other mods to find the culprit and uninstall it permanently to fix this specific problem.",
                                "* Render Player API has not been installed correctly.",
                                "  Uninstall Render Player API and install it again following the installation instructions.",
                                "========================================"
                        };

                String baseModelPlayerClassName = PlayerModelBase.class.getName();
                String targetClassName = PlayerModel.class.getName();
                String targetClassFileName = targetClassName.replace(".", File.separator);
                String stringClassName = String.class.getName();

                for (int i = 0; i < errorMessageParts.length; i++) {
                    errorMessageParts[i] = MessageFormat.format(errorMessageParts[i], baseModelPlayerClassName, targetClassName, targetClassFileName, stringClassName);
                }

                for (String errorMessagePart : errorMessageParts) {
                    logger.severe(errorMessagePart);
                }

                for (String errorMessagePart : errorMessageParts) {
                    System.err.println(errorMessagePart);
                }

                StringBuilder errorMessage = new StringBuilder("\n\n");
                for (String errorMessagePart : errorMessageParts) {
                    errorMessage.append("\t").append(errorMessagePart).append("\n");
                }

                throw new RuntimeException(errorMessage.toString(), exception);
            }

            log("Model Player " + RenderPlayerAPI.VERSION + " Created");
            isCreated = true;
        }

        if (id == null) {
            throw new NullPointerException("Argument 'id' can not be null");
        }
        if (baseClass == null) {
            throw new NullPointerException("Argument 'baseClass' can not be null");
        }

        Constructor<?> alreadyRegistered = allBaseConstructors.get(id);
        if (alreadyRegistered != null) {
            throw new IllegalArgumentException("The class '" + baseClass.getName() + "' can not be registered with the id '" + id + "' because the class '" + alreadyRegistered.getDeclaringClass().getName() + "' has already been registered with the same id");
        }

        Constructor<?> baseConstructor;
        try {
            baseConstructor = baseClass.getDeclaredConstructor(Classes);
        } catch (Throwable t) {
            try {
                baseConstructor = baseClass.getDeclaredConstructor(Class);
            } catch (Throwable s) {
                throw new IllegalArgumentException("Can not find necessary constructor with one argument of type '" + PlayerModelAPI.class.getName() + "' and eventually a second argument of type 'String' in the class '" + baseClass.getName() + "'", t);
            }
        }

        allBaseConstructors.put(id, baseConstructor);

        if (baseSorting != null) {
            addSorting(id, allBaseBeforeLocalConstructingSuperiors, baseSorting.getBeforeLocalConstructingSuperiors());
            addSorting(id, allBaseBeforeLocalConstructingInferiors, baseSorting.getBeforeLocalConstructingInferiors());
            addSorting(id, allBaseAfterLocalConstructingSuperiors, baseSorting.getAfterLocalConstructingSuperiors());
            addSorting(id, allBaseAfterLocalConstructingInferiors, baseSorting.getAfterLocalConstructingInferiors());

            addDynamicSorting(id, allBaseBeforeDynamicSuperiors, baseSorting.getDynamicBeforeSuperiors());
            addDynamicSorting(id, allBaseBeforeDynamicInferiors, baseSorting.getDynamicBeforeInferiors());
            addDynamicSorting(id, allBaseOverrideDynamicSuperiors, baseSorting.getDynamicOverrideSuperiors());
            addDynamicSorting(id, allBaseOverrideDynamicInferiors, baseSorting.getDynamicOverrideInferiors());
            addDynamicSorting(id, allBaseAfterDynamicSuperiors, baseSorting.getDynamicAfterSuperiors());
            addDynamicSorting(id, allBaseAfterDynamicInferiors, baseSorting.getDynamicAfterInferiors());

            addSorting(id, allBaseBeforeAcceptSuperiors, baseSorting.getBeforeAcceptSuperiors());
            addSorting(id, allBaseBeforeAcceptInferiors, baseSorting.getBeforeAcceptInferiors());
            addSorting(id, allBaseOverrideAcceptSuperiors, baseSorting.getOverrideAcceptSuperiors());
            addSorting(id, allBaseOverrideAcceptInferiors, baseSorting.getOverrideAcceptInferiors());
            addSorting(id, allBaseAfterAcceptSuperiors, baseSorting.getAfterAcceptSuperiors());
            addSorting(id, allBaseAfterAcceptInferiors, baseSorting.getAfterAcceptInferiors());

            addSorting(id, allBaseBeforeGetArmForSideSuperiors, baseSorting.getBeforeGetArmForSideSuperiors());
            addSorting(id, allBaseBeforeGetArmForSideInferiors, baseSorting.getBeforeGetArmForSideInferiors());
            addSorting(id, allBaseOverrideGetArmForSideSuperiors, baseSorting.getOverrideGetArmForSideSuperiors());
            addSorting(id, allBaseOverrideGetArmForSideInferiors, baseSorting.getOverrideGetArmForSideInferiors());
            addSorting(id, allBaseAfterGetArmForSideSuperiors, baseSorting.getAfterGetArmForSideSuperiors());
            addSorting(id, allBaseAfterGetArmForSideInferiors, baseSorting.getAfterGetArmForSideInferiors());

            addSorting(id, allBaseBeforeGetMainHandSuperiors, baseSorting.getBeforeGetMainHandSuperiors());
            addSorting(id, allBaseBeforeGetMainHandInferiors, baseSorting.getBeforeGetMainHandInferiors());
            addSorting(id, allBaseOverrideGetMainHandSuperiors, baseSorting.getOverrideGetMainHandSuperiors());
            addSorting(id, allBaseOverrideGetMainHandInferiors, baseSorting.getOverrideGetMainHandInferiors());
            addSorting(id, allBaseAfterGetMainHandSuperiors, baseSorting.getAfterGetMainHandSuperiors());
            addSorting(id, allBaseAfterGetMainHandInferiors, baseSorting.getAfterGetMainHandInferiors());

            addSorting(id, allBaseBeforeGetRandomModelRendererSuperiors, baseSorting.getBeforeGetRandomModelRendererSuperiors());
            addSorting(id, allBaseBeforeGetRandomModelRendererInferiors, baseSorting.getBeforeGetRandomModelRendererInferiors());
            addSorting(id, allBaseOverrideGetRandomModelRendererSuperiors, baseSorting.getOverrideGetRandomModelRendererSuperiors());
            addSorting(id, allBaseOverrideGetRandomModelRendererInferiors, baseSorting.getOverrideGetRandomModelRendererInferiors());
            addSorting(id, allBaseAfterGetRandomModelRendererSuperiors, baseSorting.getAfterGetRandomModelRendererSuperiors());
            addSorting(id, allBaseAfterGetRandomModelRendererInferiors, baseSorting.getAfterGetRandomModelRendererInferiors());

            addSorting(id, allBaseBeforeTranslateHandSuperiors, baseSorting.getBeforeTranslateHandSuperiors());
            addSorting(id, allBaseBeforeTranslateHandInferiors, baseSorting.getBeforeTranslateHandInferiors());
            addSorting(id, allBaseOverrideTranslateHandSuperiors, baseSorting.getOverrideTranslateHandSuperiors());
            addSorting(id, allBaseOverrideTranslateHandInferiors, baseSorting.getOverrideTranslateHandInferiors());
            addSorting(id, allBaseAfterTranslateHandSuperiors, baseSorting.getAfterTranslateHandSuperiors());
            addSorting(id, allBaseAfterTranslateHandInferiors, baseSorting.getAfterTranslateHandInferiors());

            addSorting(id, allBaseBeforeRenderSuperiors, baseSorting.getBeforeRenderSuperiors());
            addSorting(id, allBaseBeforeRenderInferiors, baseSorting.getBeforeRenderInferiors());
            addSorting(id, allBaseOverrideRenderSuperiors, baseSorting.getOverrideRenderSuperiors());
            addSorting(id, allBaseOverrideRenderInferiors, baseSorting.getOverrideRenderInferiors());
            addSorting(id, allBaseAfterRenderSuperiors, baseSorting.getAfterRenderSuperiors());
            addSorting(id, allBaseAfterRenderInferiors, baseSorting.getAfterRenderInferiors());

            addSorting(id, allBaseBeforeRenderCapeSuperiors, baseSorting.getBeforeRenderCapeSuperiors());
            addSorting(id, allBaseBeforeRenderCapeInferiors, baseSorting.getBeforeRenderCapeInferiors());
            addSorting(id, allBaseOverrideRenderCapeSuperiors, baseSorting.getOverrideRenderCapeSuperiors());
            addSorting(id, allBaseOverrideRenderCapeInferiors, baseSorting.getOverrideRenderCapeInferiors());
            addSorting(id, allBaseAfterRenderCapeSuperiors, baseSorting.getAfterRenderCapeSuperiors());
            addSorting(id, allBaseAfterRenderCapeInferiors, baseSorting.getAfterRenderCapeInferiors());

            addSorting(id, allBaseBeforeRenderEarsSuperiors, baseSorting.getBeforeRenderEarsSuperiors());
            addSorting(id, allBaseBeforeRenderEarsInferiors, baseSorting.getBeforeRenderEarsInferiors());
            addSorting(id, allBaseOverrideRenderEarsSuperiors, baseSorting.getOverrideRenderEarsSuperiors());
            addSorting(id, allBaseOverrideRenderEarsInferiors, baseSorting.getOverrideRenderEarsInferiors());
            addSorting(id, allBaseAfterRenderEarsSuperiors, baseSorting.getAfterRenderEarsSuperiors());
            addSorting(id, allBaseAfterRenderEarsInferiors, baseSorting.getAfterRenderEarsInferiors());

            addSorting(id, allBaseBeforeSetLivingAnimationsSuperiors, baseSorting.getBeforeSetLivingAnimationsSuperiors());
            addSorting(id, allBaseBeforeSetLivingAnimationsInferiors, baseSorting.getBeforeSetLivingAnimationsInferiors());
            addSorting(id, allBaseOverrideSetLivingAnimationsSuperiors, baseSorting.getOverrideSetLivingAnimationsSuperiors());
            addSorting(id, allBaseOverrideSetLivingAnimationsInferiors, baseSorting.getOverrideSetLivingAnimationsInferiors());
            addSorting(id, allBaseAfterSetLivingAnimationsSuperiors, baseSorting.getAfterSetLivingAnimationsSuperiors());
            addSorting(id, allBaseAfterSetLivingAnimationsInferiors, baseSorting.getAfterSetLivingAnimationsInferiors());

            addSorting(id, allBaseBeforeSetModelAttributesSuperiors, baseSorting.getBeforeSetModelAttributesSuperiors());
            addSorting(id, allBaseBeforeSetModelAttributesInferiors, baseSorting.getBeforeSetModelAttributesInferiors());
            addSorting(id, allBaseOverrideSetModelAttributesSuperiors, baseSorting.getOverrideSetModelAttributesSuperiors());
            addSorting(id, allBaseOverrideSetModelAttributesInferiors, baseSorting.getOverrideSetModelAttributesInferiors());
            addSorting(id, allBaseAfterSetModelAttributesSuperiors, baseSorting.getAfterSetModelAttributesSuperiors());
            addSorting(id, allBaseAfterSetModelAttributesInferiors, baseSorting.getAfterSetModelAttributesInferiors());

            addSorting(id, allBaseBeforeSetRotationAnglesSuperiors, baseSorting.getBeforeSetRotationAnglesSuperiors());
            addSorting(id, allBaseBeforeSetRotationAnglesInferiors, baseSorting.getBeforeSetRotationAnglesInferiors());
            addSorting(id, allBaseOverrideSetRotationAnglesSuperiors, baseSorting.getOverrideSetRotationAnglesSuperiors());
            addSorting(id, allBaseOverrideSetRotationAnglesInferiors, baseSorting.getOverrideSetRotationAnglesInferiors());
            addSorting(id, allBaseAfterSetRotationAnglesSuperiors, baseSorting.getAfterSetRotationAnglesSuperiors());
            addSorting(id, allBaseAfterSetRotationAnglesInferiors, baseSorting.getAfterSetRotationAnglesInferiors());

            addSorting(id, allBaseBeforeSetVisibleSuperiors, baseSorting.getBeforeSetVisibleSuperiors());
            addSorting(id, allBaseBeforeSetVisibleInferiors, baseSorting.getBeforeSetVisibleInferiors());
            addSorting(id, allBaseOverrideSetVisibleSuperiors, baseSorting.getOverrideSetVisibleSuperiors());
            addSorting(id, allBaseOverrideSetVisibleInferiors, baseSorting.getOverrideSetVisibleInferiors());
            addSorting(id, allBaseAfterSetVisibleSuperiors, baseSorting.getAfterSetVisibleSuperiors());
            addSorting(id, allBaseAfterSetVisibleInferiors, baseSorting.getAfterSetVisibleInferiors());
        }

        addMethod(id, baseClass, beforeLocalConstructingHookTypes, "beforeLocalConstructing", float.class, float.class, int.class, int.class, boolean.class);
        addMethod(id, baseClass, afterLocalConstructingHookTypes, "afterLocalConstructing", float.class, float.class, int.class, int.class, boolean.class);

        addMethod(id, baseClass, beforeAcceptHookTypes, "beforeAccept", ModelRenderer.class);
        addMethod(id, baseClass, overrideAcceptHookTypes, "accept", ModelRenderer.class);
        addMethod(id, baseClass, afterAcceptHookTypes, "afterAccept", ModelRenderer.class);

        addMethod(id, baseClass, beforeGetArmForSideHookTypes, "beforeGetArmForSide", HandSide.class);
        addMethod(id, baseClass, overrideGetArmForSideHookTypes, "getArmForSide", HandSide.class);
        addMethod(id, baseClass, afterGetArmForSideHookTypes, "afterGetArmForSide", HandSide.class);

        addMethod(id, baseClass, beforeGetMainHandHookTypes, "beforeGetMainHand", LivingEntity.class);
        addMethod(id, baseClass, overrideGetMainHandHookTypes, "getMainHand", LivingEntity.class);
        addMethod(id, baseClass, afterGetMainHandHookTypes, "afterGetMainHand", LivingEntity.class);

        addMethod(id, baseClass, beforeGetRandomModelRendererHookTypes, "beforeGetRandomModelRenderer", Random.class);
        addMethod(id, baseClass, overrideGetRandomModelRendererHookTypes, "getRandomModelRenderer", Random.class);
        addMethod(id, baseClass, afterGetRandomModelRendererHookTypes, "afterGetRandomModelRenderer", Random.class);

        addMethod(id, baseClass, beforeTranslateHandHookTypes, "beforeTranslateHand", HandSide.class, MatrixStack.class);
        addMethod(id, baseClass, overrideTranslateHandHookTypes, "translateHand", HandSide.class, MatrixStack.class);
        addMethod(id, baseClass, afterTranslateHandHookTypes, "afterTranslateHand", HandSide.class, MatrixStack.class);

        addMethod(id, baseClass, beforeRenderHookTypes, "beforeRender", MatrixStack.class, IVertexBuilder.class, int.class, int.class, float.class, float.class, float.class, float.class);
        addMethod(id, baseClass, overrideRenderHookTypes, "render", MatrixStack.class, IVertexBuilder.class, int.class, int.class, float.class, float.class, float.class, float.class);
        addMethod(id, baseClass, afterRenderHookTypes, "afterRender", MatrixStack.class, IVertexBuilder.class, int.class, int.class, float.class, float.class, float.class, float.class);

        addMethod(id, baseClass, beforeRenderCapeHookTypes, "beforeRenderCape", MatrixStack.class, IVertexBuilder.class, int.class, int.class);
        addMethod(id, baseClass, overrideRenderCapeHookTypes, "renderCape", MatrixStack.class, IVertexBuilder.class, int.class, int.class);
        addMethod(id, baseClass, afterRenderCapeHookTypes, "afterRenderCape", MatrixStack.class, IVertexBuilder.class, int.class, int.class);

        addMethod(id, baseClass, beforeRenderEarsHookTypes, "beforeRenderEars", MatrixStack.class, IVertexBuilder.class, int.class, int.class);
        addMethod(id, baseClass, overrideRenderEarsHookTypes, "renderEars", MatrixStack.class, IVertexBuilder.class, int.class, int.class);
        addMethod(id, baseClass, afterRenderEarsHookTypes, "afterRenderEars", MatrixStack.class, IVertexBuilder.class, int.class, int.class);

        addMethod(id, baseClass, beforeSetLivingAnimationsHookTypes, "beforeSetLivingAnimations", LivingEntity.class, float.class, float.class, float.class);
        addMethod(id, baseClass, overrideSetLivingAnimationsHookTypes, "setLivingAnimations", LivingEntity.class, float.class, float.class, float.class);
        addMethod(id, baseClass, afterSetLivingAnimationsHookTypes, "afterSetLivingAnimations", LivingEntity.class, float.class, float.class, float.class);

        addMethod(id, baseClass, beforeSetModelAttributesHookTypes, "beforeSetModelAttributes", BipedModel.class);
        addMethod(id, baseClass, overrideSetModelAttributesHookTypes, "setModelAttributes", BipedModel.class);
        addMethod(id, baseClass, afterSetModelAttributesHookTypes, "afterSetModelAttributes", BipedModel.class);

        addMethod(id, baseClass, beforeSetRotationAnglesHookTypes, "beforeSetRotationAngles", LivingEntity.class, float.class, float.class, float.class, float.class, float.class);
        addMethod(id, baseClass, overrideSetRotationAnglesHookTypes, "setRotationAngles", LivingEntity.class, float.class, float.class, float.class, float.class, float.class);
        addMethod(id, baseClass, afterSetRotationAnglesHookTypes, "afterSetRotationAngles", LivingEntity.class, float.class, float.class, float.class, float.class, float.class);

        addMethod(id, baseClass, beforeSetVisibleHookTypes, "beforeSetVisible", boolean.class);
        addMethod(id, baseClass, overrideSetVisibleHookTypes, "setVisible", boolean.class);
        addMethod(id, baseClass, afterSetVisibleHookTypes, "afterSetVisible", boolean.class);

        addDynamicMethods(id, baseClass);

        addDynamicKeys(id, baseClass, beforeDynamicHookMethods, beforeDynamicHookTypes);
        addDynamicKeys(id, baseClass, overrideDynamicHookMethods, overrideDynamicHookTypes);
        addDynamicKeys(id, baseClass, afterDynamicHookMethods, afterDynamicHookTypes);

        initialize();

        for (IPlayerModel<? extends LivingEntity> instance : PlayerModelAPI.getAllInstancesList()) {
            instance.getPlayerModelAPI().attachModelPlayerBase(id);
        }

        System.out.println("Model Player: registered " + id);
        logger.fine("Model Player: registered class '" + baseClass.getName() + "' with id '" + id + "'");

        initialized = false;
    }

    public static boolean unregister(String id)
    {
        if (id == null) {
            return false;
        }

        Constructor<?> constructor = allBaseConstructors.remove(id);
        if (constructor == null) {
            return false;
        }

        for (IPlayerModel<? extends LivingEntity> instance : PlayerModelAPI.getAllInstancesList()) {
            instance.getPlayerModelAPI().detachModelPlayerBase(id);
        }

        beforeLocalConstructingHookTypes.remove(id);
        afterLocalConstructingHookTypes.remove(id);

        allBaseBeforeAcceptSuperiors.remove(id);
        allBaseBeforeAcceptInferiors.remove(id);
        allBaseOverrideAcceptSuperiors.remove(id);
        allBaseOverrideAcceptInferiors.remove(id);
        allBaseAfterAcceptSuperiors.remove(id);
        allBaseAfterAcceptInferiors.remove(id);

        beforeAcceptHookTypes.remove(id);
        overrideAcceptHookTypes.remove(id);
        afterAcceptHookTypes.remove(id);

        allBaseBeforeGetArmForSideSuperiors.remove(id);
        allBaseBeforeGetArmForSideInferiors.remove(id);
        allBaseOverrideGetArmForSideSuperiors.remove(id);
        allBaseOverrideGetArmForSideInferiors.remove(id);
        allBaseAfterGetArmForSideSuperiors.remove(id);
        allBaseAfterGetArmForSideInferiors.remove(id);

        beforeGetArmForSideHookTypes.remove(id);
        overrideGetArmForSideHookTypes.remove(id);
        afterGetArmForSideHookTypes.remove(id);

        allBaseBeforeGetMainHandSuperiors.remove(id);
        allBaseBeforeGetMainHandInferiors.remove(id);
        allBaseOverrideGetMainHandSuperiors.remove(id);
        allBaseOverrideGetMainHandInferiors.remove(id);
        allBaseAfterGetMainHandSuperiors.remove(id);
        allBaseAfterGetMainHandInferiors.remove(id);

        beforeGetMainHandHookTypes.remove(id);
        overrideGetMainHandHookTypes.remove(id);
        afterGetMainHandHookTypes.remove(id);

        allBaseBeforeGetRandomModelRendererSuperiors.remove(id);
        allBaseBeforeGetRandomModelRendererInferiors.remove(id);
        allBaseOverrideGetRandomModelRendererSuperiors.remove(id);
        allBaseOverrideGetRandomModelRendererInferiors.remove(id);
        allBaseAfterGetRandomModelRendererSuperiors.remove(id);
        allBaseAfterGetRandomModelRendererInferiors.remove(id);

        beforeGetRandomModelRendererHookTypes.remove(id);
        overrideGetRandomModelRendererHookTypes.remove(id);
        afterGetRandomModelRendererHookTypes.remove(id);

        allBaseBeforeTranslateHandSuperiors.remove(id);
        allBaseBeforeTranslateHandInferiors.remove(id);
        allBaseOverrideTranslateHandSuperiors.remove(id);
        allBaseOverrideTranslateHandInferiors.remove(id);
        allBaseAfterTranslateHandSuperiors.remove(id);
        allBaseAfterTranslateHandInferiors.remove(id);

        beforeTranslateHandHookTypes.remove(id);
        overrideTranslateHandHookTypes.remove(id);
        afterTranslateHandHookTypes.remove(id);

        allBaseBeforeRenderSuperiors.remove(id);
        allBaseBeforeRenderInferiors.remove(id);
        allBaseOverrideRenderSuperiors.remove(id);
        allBaseOverrideRenderInferiors.remove(id);
        allBaseAfterRenderSuperiors.remove(id);
        allBaseAfterRenderInferiors.remove(id);

        beforeRenderHookTypes.remove(id);
        overrideRenderHookTypes.remove(id);
        afterRenderHookTypes.remove(id);

        allBaseBeforeRenderCapeSuperiors.remove(id);
        allBaseBeforeRenderCapeInferiors.remove(id);
        allBaseOverrideRenderCapeSuperiors.remove(id);
        allBaseOverrideRenderCapeInferiors.remove(id);
        allBaseAfterRenderCapeSuperiors.remove(id);
        allBaseAfterRenderCapeInferiors.remove(id);

        beforeRenderCapeHookTypes.remove(id);
        overrideRenderCapeHookTypes.remove(id);
        afterRenderCapeHookTypes.remove(id);

        allBaseBeforeRenderEarsSuperiors.remove(id);
        allBaseBeforeRenderEarsInferiors.remove(id);
        allBaseOverrideRenderEarsSuperiors.remove(id);
        allBaseOverrideRenderEarsInferiors.remove(id);
        allBaseAfterRenderEarsSuperiors.remove(id);
        allBaseAfterRenderEarsInferiors.remove(id);

        beforeRenderEarsHookTypes.remove(id);
        overrideRenderEarsHookTypes.remove(id);
        afterRenderEarsHookTypes.remove(id);

        allBaseBeforeSetLivingAnimationsSuperiors.remove(id);
        allBaseBeforeSetLivingAnimationsInferiors.remove(id);
        allBaseOverrideSetLivingAnimationsSuperiors.remove(id);
        allBaseOverrideSetLivingAnimationsInferiors.remove(id);
        allBaseAfterSetLivingAnimationsSuperiors.remove(id);
        allBaseAfterSetLivingAnimationsInferiors.remove(id);

        beforeSetLivingAnimationsHookTypes.remove(id);
        overrideSetLivingAnimationsHookTypes.remove(id);
        afterSetLivingAnimationsHookTypes.remove(id);

        allBaseBeforeSetModelAttributesSuperiors.remove(id);
        allBaseBeforeSetModelAttributesInferiors.remove(id);
        allBaseOverrideSetModelAttributesSuperiors.remove(id);
        allBaseOverrideSetModelAttributesInferiors.remove(id);
        allBaseAfterSetModelAttributesSuperiors.remove(id);
        allBaseAfterSetModelAttributesInferiors.remove(id);

        beforeSetModelAttributesHookTypes.remove(id);
        overrideSetModelAttributesHookTypes.remove(id);
        afterSetModelAttributesHookTypes.remove(id);

        allBaseBeforeSetRotationAnglesSuperiors.remove(id);
        allBaseBeforeSetRotationAnglesInferiors.remove(id);
        allBaseOverrideSetRotationAnglesSuperiors.remove(id);
        allBaseOverrideSetRotationAnglesInferiors.remove(id);
        allBaseAfterSetRotationAnglesSuperiors.remove(id);
        allBaseAfterSetRotationAnglesInferiors.remove(id);

        beforeSetRotationAnglesHookTypes.remove(id);
        overrideSetRotationAnglesHookTypes.remove(id);
        afterSetRotationAnglesHookTypes.remove(id);

        allBaseBeforeSetVisibleSuperiors.remove(id);
        allBaseBeforeSetVisibleInferiors.remove(id);
        allBaseOverrideSetVisibleSuperiors.remove(id);
        allBaseOverrideSetVisibleInferiors.remove(id);
        allBaseAfterSetVisibleSuperiors.remove(id);
        allBaseAfterSetVisibleInferiors.remove(id);

        beforeSetVisibleHookTypes.remove(id);
        overrideSetVisibleHookTypes.remove(id);
        afterSetVisibleHookTypes.remove(id);

        for (IPlayerModel<? extends LivingEntity> instance : PlayerModelAPI.getAllInstancesList()) {
            instance.getPlayerModelAPI().updateModelPlayerBases();
        }

        Iterator<String> iterator = keysToVirtualIds.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (keysToVirtualIds.get(key).equals(id)) {
                keysToVirtualIds.remove(key);
            }
        }

        boolean otherFound = false;
        Class<?> type = constructor.getDeclaringClass();

        iterator = allBaseConstructors.keySet().iterator();
        while (iterator.hasNext()) {
            String otherId = iterator.next();
            Class<?> otherType = allBaseConstructors.get(otherId).getDeclaringClass();
            if (!otherId.equals(id) && otherType.equals(type)) {
                otherFound = true;
                break;
            }
        }

        if (!otherFound) {
            dynamicTypes.remove(type);

            virtualDynamicHookMethods.remove(type);

            beforeDynamicHookMethods.remove(type);
            overrideDynamicHookMethods.remove(type);
            afterDynamicHookMethods.remove(type);
        }

        removeDynamicHookTypes(id, beforeDynamicHookTypes);
        removeDynamicHookTypes(id, overrideDynamicHookTypes);
        removeDynamicHookTypes(id, afterDynamicHookTypes);

        allBaseBeforeDynamicSuperiors.remove(id);
        allBaseBeforeDynamicInferiors.remove(id);
        allBaseOverrideDynamicSuperiors.remove(id);
        allBaseOverrideDynamicInferiors.remove(id);
        allBaseAfterDynamicSuperiors.remove(id);
        allBaseAfterDynamicInferiors.remove(id);

        log("ModelPlayerAPI: unregistered id '" + id + "'");

        return true;
    }

    public static void removeDynamicHookTypes(String id, Map<String, List<String>> map)
    {
        for (String s : map.keySet()) {
            map.get(s).remove(id);
        }
    }

    public static Set<String> getRegisteredIds()
    {
        return unmodifiableAllIds;
    }

    private static void addSorting(String id, Map<String, String[]> map, String[] values)
    {
        if (values != null && values.length > 0) {
            map.put(id, values);
        }
    }

    private static void addDynamicSorting(String id, Map<String, Map<String, String[]>> map, Map<String, String[]> values)
    {
        if (values != null && values.size() > 0) {
            map.put(id, values);
        }
    }

    private static void addMethod(String id, Class<?> baseClass, List<String> list, String methodName, Class<?>... _parameterTypes)
    {
        try {
            Method method = baseClass.getMethod(methodName, _parameterTypes);
            boolean isOverridden = method.getDeclaringClass() != PlayerModelBase.class;
            if (isOverridden) {
                list.add(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not reflect method '" + methodName + "' of class '" + baseClass.getName() + "'", e);
        }
    }

    private static void addDynamicMethods(String id, Class<?> baseClass)
    {
        if (!dynamicTypes.add(baseClass)) {
            return;
        }

        Map<String, Method> virtuals = null;
        Map<String, Method> befores = null;
        Map<String, Method> overrides = null;
        Map<String, Method> afters = null;

        Method[] methods = baseClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != baseClass) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                continue;
            }

            if (Modifier.isStatic(modifiers)) {
                continue;
            }

            String name = method.getName();
            if (name.length() < 7 || !name.substring(0, 7).equalsIgnoreCase("dynamic")) {
                continue;
            } else {
                name = name.substring(7);
            }

            while (name.charAt(0) == '_') {
                name = name.substring(1);
            }

            boolean before = false;
            boolean virtual = false;
            boolean override = false;
            boolean after = false;

            if (name.substring(0, 7).equalsIgnoreCase("virtual")) {
                virtual = true;
                name = name.substring(7);
            } else {
                if (name.length() >= 8 && name.substring(0, 8).equalsIgnoreCase("override")) {
                    name = name.substring(8);
                    override = true;
                } else if (name.substring(0, 6).equalsIgnoreCase("before")) {
                    before = true;
                    name = name.substring(6);
                } else if (name.substring(0, 5).equalsIgnoreCase("after")) {
                    after = true;
                    name = name.substring(5);
                }
            }

            if (name.length() >= 1 && (before || virtual || override || after)) {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            }

            while (name.charAt(0) == '_') {
                name = name.substring(1);
            }

            keys.add(name);

            if (virtual) {
                if (keysToVirtualIds.containsKey(name)) {
                    throw new RuntimeException("Can not process more than one dynamic virtual method");
                }

                keysToVirtualIds.put(name, id);
                virtuals = addDynamicMethod(name, method, virtuals);
            } else if (before) {
                befores = addDynamicMethod(name, method, befores);
            } else if (after) {
                afters = addDynamicMethod(name, method, afters);
            } else {
                overrides = addDynamicMethod(name, method, overrides);
            }
        }

        if (virtuals != null) {
            virtualDynamicHookMethods.put(baseClass, virtuals);
        }
        if (befores != null) {
            beforeDynamicHookMethods.put(baseClass, befores);
        }
        if (overrides != null) {
            overrideDynamicHookMethods.put(baseClass, overrides);
        }
        if (afters != null) {
            afterDynamicHookMethods.put(baseClass, afters);
        }
    }

    private static void addDynamicKeys(String id, Class<?> baseClass, Map<Class<?>, Map<String, Method>> dynamicHookMethods, Map<String, List<String>> dynamicHookTypes)
    {
        Map<String, Method> methods = dynamicHookMethods.get(baseClass);
        if (methods == null || methods.size() == 0) {
            return;
        }

        for (String key : methods.keySet()) {
            if (!dynamicHookTypes.containsKey(key)) {
                dynamicHookTypes.put(key, new ArrayList<>(1));
            }
            dynamicHookTypes.get(key).add(id);
        }
    }

    private static Map<String, Method> addDynamicMethod(String key, Method method, Map<String, Method> methods)
    {
        if (methods == null) {
            methods = new HashMap<>();
        }
        if (methods.containsKey(key)) {
            throw new RuntimeException("method with key '" + key + "' already exists");
        }
        methods.put(key, method);
        return methods;
    }

    public static <T extends LivingEntity> PlayerModelAPI<T> create(IPlayerModel<T> modelPlayer, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
        if (allBaseConstructors.size() > 0 && !initialized) {
            initialize();
        }
        return new PlayerModelAPI<>(modelPlayer, paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
    }

    private static void initialize()
    {
        sortBases(beforeLocalConstructingHookTypes, allBaseBeforeLocalConstructingSuperiors, allBaseBeforeLocalConstructingInferiors, "beforeLocalConstructing");
        sortBases(afterLocalConstructingHookTypes, allBaseAfterLocalConstructingSuperiors, allBaseAfterLocalConstructingInferiors, "afterLocalConstructing");

        for (String key : keys) {
            sortDynamicBases(beforeDynamicHookTypes, allBaseBeforeDynamicSuperiors, allBaseBeforeDynamicInferiors, key);
            sortDynamicBases(overrideDynamicHookTypes, allBaseOverrideDynamicSuperiors, allBaseOverrideDynamicInferiors, key);
            sortDynamicBases(afterDynamicHookTypes, allBaseAfterDynamicSuperiors, allBaseAfterDynamicInferiors, key);
        }

        sortBases(beforeAcceptHookTypes, allBaseBeforeAcceptSuperiors, allBaseBeforeAcceptInferiors, "beforeAccept");
        sortBases(overrideAcceptHookTypes, allBaseOverrideAcceptSuperiors, allBaseOverrideAcceptInferiors, "overrideAccept");
        sortBases(afterAcceptHookTypes, allBaseAfterAcceptSuperiors, allBaseAfterAcceptInferiors, "afterAccept");

        sortBases(beforeGetArmForSideHookTypes, allBaseBeforeGetArmForSideSuperiors, allBaseBeforeGetArmForSideInferiors, "beforeGetArmForSide");
        sortBases(overrideGetArmForSideHookTypes, allBaseOverrideGetArmForSideSuperiors, allBaseOverrideGetArmForSideInferiors, "overrideGetArmForSide");
        sortBases(afterGetArmForSideHookTypes, allBaseAfterGetArmForSideSuperiors, allBaseAfterGetArmForSideInferiors, "afterGetArmForSide");

        sortBases(beforeGetMainHandHookTypes, allBaseBeforeGetMainHandSuperiors, allBaseBeforeGetMainHandInferiors, "beforeGetMainHand");
        sortBases(overrideGetMainHandHookTypes, allBaseOverrideGetMainHandSuperiors, allBaseOverrideGetMainHandInferiors, "overrideGetMainHand");
        sortBases(afterGetMainHandHookTypes, allBaseAfterGetMainHandSuperiors, allBaseAfterGetMainHandInferiors, "afterGetMainHand");

        sortBases(beforeGetRandomModelRendererHookTypes, allBaseBeforeGetRandomModelRendererSuperiors, allBaseBeforeGetRandomModelRendererInferiors, "beforeGetRandomModelBox");
        sortBases(overrideGetRandomModelRendererHookTypes, allBaseOverrideGetRandomModelRendererSuperiors, allBaseOverrideGetRandomModelRendererInferiors, "overrideGetRandomModelBox");
        sortBases(afterGetRandomModelRendererHookTypes, allBaseAfterGetRandomModelRendererSuperiors, allBaseAfterGetRandomModelRendererInferiors, "afterGetRandomModelBox");

        sortBases(beforeTranslateHandHookTypes, allBaseBeforeTranslateHandSuperiors, allBaseBeforeTranslateHandInferiors, "beforeTranslateHand");
        sortBases(overrideTranslateHandHookTypes, allBaseOverrideTranslateHandSuperiors, allBaseOverrideTranslateHandInferiors, "overrideTranslateHand");
        sortBases(afterTranslateHandHookTypes, allBaseAfterTranslateHandSuperiors, allBaseAfterTranslateHandInferiors, "afterTranslateHand");

        sortBases(beforeRenderHookTypes, allBaseBeforeRenderSuperiors, allBaseBeforeRenderInferiors, "beforeRender");
        sortBases(overrideRenderHookTypes, allBaseOverrideRenderSuperiors, allBaseOverrideRenderInferiors, "overrideRender");
        sortBases(afterRenderHookTypes, allBaseAfterRenderSuperiors, allBaseAfterRenderInferiors, "afterRender");

        sortBases(beforeRenderCapeHookTypes, allBaseBeforeRenderCapeSuperiors, allBaseBeforeRenderCapeInferiors, "beforeRenderCape");
        sortBases(overrideRenderCapeHookTypes, allBaseOverrideRenderCapeSuperiors, allBaseOverrideRenderCapeInferiors, "overrideRenderCape");
        sortBases(afterRenderCapeHookTypes, allBaseAfterRenderCapeSuperiors, allBaseAfterRenderCapeInferiors, "afterRenderCape");

        sortBases(beforeRenderEarsHookTypes, allBaseBeforeRenderEarsSuperiors, allBaseBeforeRenderEarsInferiors, "beforeRenderEars");
        sortBases(overrideRenderEarsHookTypes, allBaseOverrideRenderEarsSuperiors, allBaseOverrideRenderEarsInferiors, "overrideRenderEars");
        sortBases(afterRenderEarsHookTypes, allBaseAfterRenderEarsSuperiors, allBaseAfterRenderEarsInferiors, "afterRenderEars");

        sortBases(beforeSetLivingAnimationsHookTypes, allBaseBeforeSetLivingAnimationsSuperiors, allBaseBeforeSetLivingAnimationsInferiors, "beforeSetLivingAnimations");
        sortBases(overrideSetLivingAnimationsHookTypes, allBaseOverrideSetLivingAnimationsSuperiors, allBaseOverrideSetLivingAnimationsInferiors, "overrideSetLivingAnimations");
        sortBases(afterSetLivingAnimationsHookTypes, allBaseAfterSetLivingAnimationsSuperiors, allBaseAfterSetLivingAnimationsInferiors, "afterSetLivingAnimations");

        sortBases(beforeSetModelAttributesHookTypes, allBaseBeforeSetModelAttributesSuperiors, allBaseBeforeSetModelAttributesInferiors, "beforeSetModelAttributes");
        sortBases(overrideSetModelAttributesHookTypes, allBaseOverrideSetModelAttributesSuperiors, allBaseOverrideSetModelAttributesInferiors, "overrideSetModelAttributes");
        sortBases(afterSetModelAttributesHookTypes, allBaseAfterSetModelAttributesSuperiors, allBaseAfterSetModelAttributesInferiors, "afterSetModelAttributes");

        sortBases(beforeSetRotationAnglesHookTypes, allBaseBeforeSetRotationAnglesSuperiors, allBaseBeforeSetRotationAnglesInferiors, "beforeSetRotationAngles");
        sortBases(overrideSetRotationAnglesHookTypes, allBaseOverrideSetRotationAnglesSuperiors, allBaseOverrideSetRotationAnglesInferiors, "overrideSetRotationAngles");
        sortBases(afterSetRotationAnglesHookTypes, allBaseAfterSetRotationAnglesSuperiors, allBaseAfterSetRotationAnglesInferiors, "afterSetRotationAngles");

        sortBases(beforeSetVisibleHookTypes, allBaseBeforeSetVisibleSuperiors, allBaseBeforeSetVisibleInferiors, "beforeSetVisible");
        sortBases(overrideSetVisibleHookTypes, allBaseOverrideSetVisibleSuperiors, allBaseOverrideSetVisibleInferiors, "overrideSetVisible");
        sortBases(afterSetVisibleHookTypes, allBaseAfterSetVisibleSuperiors, allBaseAfterSetVisibleInferiors, "afterSetVisible");

        initialized = true;
    }

    private static List<IPlayerModel<? extends LivingEntity>> getAllInstancesList()
    {
        List<IPlayerModel<? extends LivingEntity>> result = new ArrayList<>();
        for (Iterator<WeakReference<IPlayerModel<? extends LivingEntity>>> iterator = allInstances.iterator(); iterator.hasNext(); ) {
            IPlayerModel<? extends LivingEntity> instance = iterator.next().get();
            if (instance != null) {
                result.add(instance);
            } else {
                iterator.remove();
            }
        }
        return result;
    }

    private static final List<WeakReference<IPlayerModel<? extends LivingEntity>>> allInstances = new ArrayList<>();

    public static <T extends LivingEntity> BipedModel<T>[] getAllInstances()
    {
        return getAllInstancesList().stream().map(instance -> (BipedModel<T>) instance).toArray(BipedModel[]::new);
    }

    public static <T extends LivingEntity> void beforeLocalConstructing(IPlayerModel<T> modelPlayer, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
        PlayerModelAPI<T> playerModelAPI = modelPlayer.getPlayerModelAPI();
        if (playerModelAPI != null) {
            playerModelAPI.load();
        }

        PlayerModelAPI.allInstances.add(new WeakReference<>(modelPlayer));

        if (playerModelAPI != null) {
            playerModelAPI.beforeLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
        }
    }

    public static <T extends LivingEntity> void afterLocalConstructing(IPlayerModel<T> modelPlayer, float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
        PlayerModelAPI<T> playerModelAPI = modelPlayer.getPlayerModelAPI();
        if (playerModelAPI != null) {
            playerModelAPI.afterLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
        }
    }

    public static <T extends LivingEntity> PlayerModelBase<T> getPlayerModelBase(IPlayerModel<T> modelPlayer, String baseId)
    {
        PlayerModelAPI<T> playerModelAPI = modelPlayer.getPlayerModelAPI();
        if (playerModelAPI != null) {
            return playerModelAPI.getPlayerModelBase(baseId);
        }
        return null;
    }

    public static <T extends LivingEntity> Set<String> getPlayerModelBaseIds(IPlayerModel<T> modelPlayer)
    {
        PlayerModelAPI<T> playerModelAPI = modelPlayer.getPlayerModelAPI();
        Set<String> result;
        if (playerModelAPI != null) {
            result = playerModelAPI.getPlayerModelBaseIds();
        } else {
            result = Collections.emptySet();
        }
        return result;
    }

    public static <T extends LivingEntity> float getTextureOffsetX(IPlayerModel<T> modelPlayer)
    {
        PlayerModelAPI<T> modelPlayerAPI = modelPlayer.getPlayerModelAPI();
        if (modelPlayerAPI != null) {
            return modelPlayerAPI.textureOffsetX;
        }
        return 0;
    }

    public static <T extends LivingEntity> float getTextureOffsetY(IPlayerModel<T> modelPlayer)
    {
        PlayerModelAPI<T> modelPlayerAPI = modelPlayer.getPlayerModelAPI();
        if (modelPlayerAPI != null) {
            return modelPlayerAPI.textureOffsetY;
        }
        return 0;
    }

    public static <T extends LivingEntity> int getTextureWidth(IPlayerModel<T> modelPlayer)
    {
        PlayerModelAPI<T> modelPlayerAPI = modelPlayer.getPlayerModelAPI();
        if (modelPlayerAPI != null) {
            return modelPlayerAPI.textureWidth;
        }
        return 0;
    }

    public static <T extends LivingEntity> int getTextureHeight(IPlayerModel<T> modelPlayer)
    {
        PlayerModelAPI<T> modelPlayerAPI = modelPlayer.getPlayerModelAPI();
        if (modelPlayerAPI != null) {
            return modelPlayerAPI.textureHeight;
        }
        return 0;
    }

    public static <T extends LivingEntity> boolean getSmallArms(IPlayerModel<T> iPlayerModel)
    {
        PlayerModelAPI<T> modelPlayerAPI = iPlayerModel.getPlayerModelAPI();
        if (modelPlayerAPI != null) {
            return modelPlayerAPI.smallArms;
        }
        return false;
    }

    public static <T extends LivingEntity> Object dynamic(IPlayerModel<T> modelPlayer, String key, Object[] parameters)
    {
        PlayerModelAPI<T> playerModelAPI = modelPlayer.getPlayerModelAPI();
        if (playerModelAPI != null) {
            return playerModelAPI.dynamic(key, parameters);
        }
        return null;
    }

    private static void sortBases(List<String> list, Map<String, String[]> allBaseSuperiors, Map<String, String[]> allBaseInferiors, String methodName)
    {
        new PlayerModelBaseSorter(list, allBaseSuperiors, allBaseInferiors, methodName).Sort();
    }

    private final static Map<String, String[]> EmptySortMap = Collections.unmodifiableMap(new HashMap<>());

    private static void sortDynamicBases(Map<String, List<String>> lists, Map<String, Map<String, String[]>> allBaseSuperiors, Map<String, Map<String, String[]>> allBaseInferiors, String key)
    {
        List<String> types = lists.get(key);
        if (types != null && types.size() > 1) {
            sortBases(types, getDynamicSorters(key, types, allBaseSuperiors), getDynamicSorters(key, types, allBaseInferiors), key);
        }
    }

    private static Map<String, String[]> getDynamicSorters(String key, List<String> toSort, Map<String, Map<String, String[]>> allBaseValues)
    {
        Map<String, String[]> superiors = null;

        for (String id : toSort) {
            Map<String, String[]> idSuperiors = allBaseValues.get(id);
            if (idSuperiors == null) {
                continue;
            }

            String[] keySuperiorIds = idSuperiors.get(key);
            if (keySuperiorIds != null && keySuperiorIds.length > 0) {
                if (superiors == null) {
                    superiors = new HashMap<>(1);
                }
                superiors.put(id, keySuperiorIds);
            }
        }

        return superiors != null ? superiors : EmptySortMap;
    }

    private PlayerModelAPI(IPlayerModel<T> iPlayerModel, float textureOffsetX, float textureOffsetY, int textureWidth, int textureHeight, boolean smallArms)
    {
        this.iPlayerModel = iPlayerModel;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.smallArms = smallArms;
    }

    private void load()
    {
        Iterator<String> iterator = allBaseConstructors.keySet().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            PlayerModelBase<T> toAttach = this.createModelPlayerBase(id);
            toAttach.beforeBaseAttach(false);
            this.allBaseObjects.put(id, toAttach);
            this.baseObjectsToId.put(toAttach, id);
        }

        this.beforeLocalConstructingHooks = this.create(beforeLocalConstructingHookTypes);
        this.afterLocalConstructingHooks = this.create(afterLocalConstructingHookTypes);

        this.updateModelPlayerBases();

        iterator = this.allBaseObjects.keySet().iterator();
        while (iterator.hasNext()) {
            this.allBaseObjects.get(iterator.next()).afterBaseAttach(false);
        }
    }

    private PlayerModelBase<T> createModelPlayerBase(String id)
    {
        Constructor<?> constructor = allBaseConstructors.get(id);

        PlayerModelBase<T> base;
        try {
            if (constructor.getParameterTypes().length == 1) {
                base = (PlayerModelBase<T>) constructor.newInstance(this);
            } else {
                base = (PlayerModelBase<T>) constructor.newInstance(this, id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating a ModelPlayerBase of type '" + constructor.getDeclaringClass() + "'", e);
        }
        return base;
    }

    private void updateModelPlayerBases()
    {
        this.beforeAcceptHooks = this.create(beforeAcceptHookTypes);
        this.overrideAcceptHooks = this.create(overrideAcceptHookTypes);
        this.afterAcceptHooks = this.create(afterAcceptHookTypes);
        this.isAcceptModded =
                this.beforeAcceptHooks != null ||
                        this.overrideAcceptHooks != null ||
                        this.afterAcceptHooks != null;

        this.beforeGetArmForSideHooks = this.create(beforeGetArmForSideHookTypes);
        this.overrideGetArmForSideHooks = this.create(overrideGetArmForSideHookTypes);
        this.afterGetArmForSideHooks = this.create(afterGetArmForSideHookTypes);
        this.isGetArmForSideModded =
                this.beforeGetArmForSideHooks != null ||
                        this.overrideGetArmForSideHooks != null ||
                        this.afterGetArmForSideHooks != null;

        this.beforeGetMainHandHooks = this.create(beforeGetMainHandHookTypes);
        this.overrideGetMainHandHooks = this.create(overrideGetMainHandHookTypes);
        this.afterGetMainHandHooks = this.create(afterGetMainHandHookTypes);
        this.isGetMainHandModded =
                this.beforeGetMainHandHooks != null ||
                        this.overrideGetMainHandHooks != null ||
                        this.afterGetMainHandHooks != null;

        this.beforeGetRandomModelRendererHooks = this.create(beforeGetRandomModelRendererHookTypes);
        this.overrideGetRandomModelRendererHooks = this.create(overrideGetRandomModelRendererHookTypes);
        this.afterGetRandomModelRendererHooks = this.create(afterGetRandomModelRendererHookTypes);
        this.isGetRandomModelRendererModded =
                this.beforeGetRandomModelRendererHooks != null ||
                        this.overrideGetRandomModelRendererHooks != null ||
                        this.afterGetRandomModelRendererHooks != null;

        this.beforeTranslateHandHooks = this.create(beforeTranslateHandHookTypes);
        this.overrideTranslateHandHooks = this.create(overrideTranslateHandHookTypes);
        this.afterTranslateHandHooks = this.create(afterTranslateHandHookTypes);
        this.isTranslateHandModded =
                this.beforeTranslateHandHooks != null ||
                        this.overrideTranslateHandHooks != null ||
                        this.afterTranslateHandHooks != null;

        this.beforeRenderHooks = this.create(beforeRenderHookTypes);
        this.overrideRenderHooks = this.create(overrideRenderHookTypes);
        this.afterRenderHooks = this.create(afterRenderHookTypes);
        this.isRenderModded =
                this.beforeRenderHooks != null ||
                        this.overrideRenderHooks != null ||
                        this.afterRenderHooks != null;

        this.beforeRenderCapeHooks = this.create(beforeRenderCapeHookTypes);
        this.overrideRenderCapeHooks = this.create(overrideRenderCapeHookTypes);
        this.afterRenderCapeHooks = this.create(afterRenderCapeHookTypes);
        this.isRenderCapeModded =
                this.beforeRenderCapeHooks != null ||
                        this.overrideRenderCapeHooks != null ||
                        this.afterRenderCapeHooks != null;

        this.beforeRenderEarsHooks = this.create(beforeRenderEarsHookTypes);
        this.overrideRenderEarsHooks = this.create(overrideRenderEarsHookTypes);
        this.afterRenderEarsHooks = this.create(afterRenderEarsHookTypes);
        this.isRenderEarsModded =
                this.beforeRenderEarsHooks != null ||
                        this.overrideRenderEarsHooks != null ||
                        this.afterRenderEarsHooks != null;

        this.beforeSetLivingAnimationsHooks = this.create(beforeSetLivingAnimationsHookTypes);
        this.overrideSetLivingAnimationsHooks = this.create(overrideSetLivingAnimationsHookTypes);
        this.afterSetLivingAnimationsHooks = this.create(afterSetLivingAnimationsHookTypes);
        this.isSetLivingAnimationsModded =
                this.beforeSetLivingAnimationsHooks != null ||
                        this.overrideSetLivingAnimationsHooks != null ||
                        this.afterSetLivingAnimationsHooks != null;

        this.beforeSetModelAttributesHooks = this.create(beforeSetModelAttributesHookTypes);
        this.overrideSetModelAttributesHooks = this.create(overrideSetModelAttributesHookTypes);
        this.afterSetModelAttributesHooks = this.create(afterSetModelAttributesHookTypes);
        this.isSetModelAttributesModded =
                this.beforeSetModelAttributesHooks != null ||
                        this.overrideSetModelAttributesHooks != null ||
                        this.afterSetModelAttributesHooks != null;

        this.beforeSetRotationAnglesHooks = this.create(beforeSetRotationAnglesHookTypes);
        this.overrideSetRotationAnglesHooks = this.create(overrideSetRotationAnglesHookTypes);
        this.afterSetRotationAnglesHooks = this.create(afterSetRotationAnglesHookTypes);
        this.isSetRotationAnglesModded =
                this.beforeSetRotationAnglesHooks != null ||
                        this.overrideSetRotationAnglesHooks != null ||
                        this.afterSetRotationAnglesHooks != null;

        this.beforeSetVisibleHooks = this.create(beforeSetVisibleHookTypes);
        this.overrideSetVisibleHooks = this.create(overrideSetVisibleHookTypes);
        this.afterSetVisibleHooks = this.create(afterSetVisibleHookTypes);
        this.isSetVisibleModded =
                this.beforeSetVisibleHooks != null ||
                        this.overrideSetVisibleHooks != null ||
                        this.afterSetVisibleHooks != null;
    }

    private void attachModelPlayerBase(String id)
    {
        PlayerModelBase<T> toAttach = this.createModelPlayerBase(id);
        toAttach.beforeBaseAttach(true);
        this.allBaseObjects.put(id, toAttach);
        this.updateModelPlayerBases();
        toAttach.afterBaseAttach(true);
    }

    private void detachModelPlayerBase(String id)
    {
        PlayerModelBase<T> toDetach = this.allBaseObjects.get(id);
        toDetach.beforeBaseDetach(true);
        this.allBaseObjects.remove(id);
        toDetach.afterBaseDetach(true);
    }

    private PlayerModelBase<T>[] create(List<String> types)
    {
        if (types.isEmpty()) {
            return null;
        }

        PlayerModelBase<T>[] result = new PlayerModelBase[types.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.getPlayerModelBase(types.get(i));
        }
        return result;
    }

    private void beforeLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
        if (this.beforeLocalConstructingHooks != null) {
            for (int i = this.beforeLocalConstructingHooks.length - 1; i >= 0; i--) {
                this.beforeLocalConstructingHooks[i].beforeLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
            }
        }
        this.beforeLocalConstructingHooks = null;
    }

    private void afterLocalConstructing(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, boolean paramBoolean)
    {
        if (this.afterLocalConstructingHooks != null) {
            for (PlayerModelBase<T> afterLocalConstructingHook : this.afterLocalConstructingHooks) {
                afterLocalConstructingHook.afterLocalConstructing(paramFloat1, paramFloat2, paramInt1, paramInt2, paramBoolean);
            }
        }
        this.afterLocalConstructingHooks = null;
    }

    public PlayerModelBase<T> getPlayerModelBase(String id)
    {
        return this.allBaseObjects.get(id);
    }

    public Set<String> getPlayerModelBaseIds()
    {
        return this.unmodifiableAllBaseIds;
    }

    public Object dynamic(String key, Object[] parameters)
    {
        key = key.replace('.', '_').replace(' ', '_');
        this.executeAll(key, parameters, beforeDynamicHookTypes, beforeDynamicHookMethods, true);
        Object result = this.dynamicOverwritten(key, parameters, null);
        this.executeAll(key, parameters, afterDynamicHookTypes, afterDynamicHookMethods, false);
        return result;
    }

    public Object dynamicOverwritten(String key, Object[] parameters, PlayerModelBase<T> overwriter)
    {
        List<String> overrideIds = overrideDynamicHookTypes.get(key);

        String id = null;
        if (overrideIds != null) {
            if (overwriter != null) {
                id = this.baseObjectsToId.get(overwriter);
                int index = overrideIds.indexOf(id);
                if (index > 0) {
                    id = overrideIds.get(index - 1);
                } else {
                    id = null;
                }
            } else if (overrideIds.size() > 0) {
                id = overrideIds.get(overrideIds.size() - 1);
            }
        }

        Map<Class<?>, Map<String, Method>> methodMap;

        if (id == null) {
            id = keysToVirtualIds.get(key);
            if (id == null) {
                return null;
            }
            methodMap = virtualDynamicHookMethods;
        } else {
            methodMap = overrideDynamicHookMethods;
        }

        Map<String, Method> methods = methodMap.get(allBaseConstructors.get(id).getDeclaringClass());
        if (methods == null) {
            return null;
        }

        Method method = methods.get(key);
        if (method == null) {
            return null;
        }

        return this.execute(this.getPlayerModelBase(id), method, parameters);
    }

    private void executeAll(String key, Object[] parameters, Map<String, List<String>> dynamicHookTypes, Map<Class<?>, Map<String, Method>> dynamicHookMethods, boolean reverse)
    {
        List<String> beforeIds = dynamicHookTypes.get(key);
        if (beforeIds == null) {
            return;
        }

        for (int i = reverse ? beforeIds.size() - 1 : 0; reverse ? i >= 0 : i < beforeIds.size(); i = i + (reverse ? -1 : 1)) {
            String id = beforeIds.get(i);
            PlayerModelBase<T> base = this.getPlayerModelBase(id);
            Class<?> type = base.getClass();

            Map<String, Method> methods = dynamicHookMethods.get(type);
            if (methods == null) {
                continue;
            }

            Method method = methods.get(key);
            if (method == null) {
                continue;
            }

            this.execute(base, method, parameters);
        }
    }

    private Object execute(PlayerModelBase<T> base, Method method, Object[] parameters)
    {
        try {
            return method.invoke(base, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Exception while invoking dynamic method", e);
        }
    }

    // ############################################################################

    public static <T extends LivingEntity> void beforeAccept(CallbackInfo callbackInfo, IPlayerModel<T> target, ModelRenderer renderer)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isAcceptModded) {
            playerModelAPI.beforeAccept(callbackInfo, renderer);
        }
    }

    private void beforeAccept(CallbackInfo callbackInfo, ModelRenderer renderer)
    {
        if (this.beforeAcceptHooks != null) {
            for (int i = this.beforeAcceptHooks.length - 1; i >= 0; i--) {
                this.beforeAcceptHooks[i].beforeAccept(renderer);
            }
        }

        if (this.overrideAcceptHooks != null) {
            this.overrideAcceptHooks[this.overrideAcceptHooks.length - 1].accept(renderer);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterAccept(IPlayerModel<T> target, ModelRenderer renderer)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isAcceptModded) {
            playerModelAPI.afterAccept(renderer);
        }
    }

    private void afterAccept(ModelRenderer renderer)
    {
        if (this.afterAcceptHooks != null) {
            for (PlayerModelBase<T> afterAcceptHook : this.afterAcceptHooks) {
                afterAcceptHook.afterAccept(renderer);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenAccept(PlayerModelBase<T> overwriter)
    {
        if (this.overrideAcceptHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideAcceptHooks.length; i++) {
            if (this.overrideAcceptHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideAcceptHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeAcceptHookTypes = new LinkedList<>();
    private final static List<String> overrideAcceptHookTypes = new LinkedList<>();
    private final static List<String> afterAcceptHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeAcceptHooks;
    private PlayerModelBase<T>[] overrideAcceptHooks;
    private PlayerModelBase<T>[] afterAcceptHooks;
    public boolean isAcceptModded;
    private static final Map<String, String[]> allBaseBeforeAcceptSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeAcceptInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideAcceptSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideAcceptInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterAcceptSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterAcceptInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> ModelRenderer getArmForSide(IPlayerModel<T> target, HandSide side)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isGetArmForSideModded) {
            return playerModelAPI.getArmForSide(side);
        } else {
            return target.superGetArmForSide(side);
        }
    }

    private ModelRenderer getArmForSide(HandSide side)
    {
        if (this.beforeGetArmForSideHooks != null) {
            for (int i = this.beforeGetArmForSideHooks.length - 1; i >= 0; i--) {
                this.beforeGetArmForSideHooks[i].beforeGetArmForSide(side);
            }
        }

        ModelRenderer result;
        if (this.overrideGetArmForSideHooks != null) {
            result = this.overrideGetArmForSideHooks[this.overrideGetArmForSideHooks.length - 1].getArmForSide(side);
        } else {
            result = this.iPlayerModel.superGetArmForSide(side);
        }

        if (this.afterGetArmForSideHooks != null) {
            for (PlayerModelBase<T> afterGetArmForSideHook : this.afterGetArmForSideHooks) {
                afterGetArmForSideHook.afterGetArmForSide(side);
            }
        }

        return result;
    }

    protected PlayerModelBase<T> getOverwrittenGetArmForSide(PlayerModelBase<T> overwriter)
    {
        if (this.overrideGetArmForSideHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetArmForSideHooks.length; i++) {
            if (this.overrideGetArmForSideHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetArmForSideHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetArmForSideHookTypes = new LinkedList<>();
    private final static List<String> overrideGetArmForSideHookTypes = new LinkedList<>();
    private final static List<String> afterGetArmForSideHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeGetArmForSideHooks;
    private PlayerModelBase<T>[] overrideGetArmForSideHooks;
    private PlayerModelBase<T>[] afterGetArmForSideHooks;
    public boolean isGetArmForSideModded;
    private static final Map<String, String[]> allBaseBeforeGetArmForSideSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetArmForSideInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetArmForSideSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetArmForSideInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetArmForSideSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetArmForSideInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> HandSide getMainHand(IPlayerModel<T> target, T livingEntity)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isGetArmForSideModded) {
            return playerModelAPI.getMainHand(livingEntity);
        } else {
            return target.superGetMainHand(livingEntity);
        }
    }

    public HandSide getMainHand(T livingEntity)
    {
        if (this.beforeGetMainHandHooks != null) {
            for (int i = this.beforeGetMainHandHooks.length - 1; i >= 0; i--) {
                this.beforeGetMainHandHooks[i].beforeGetMainHand(livingEntity);
            }
        }

        HandSide result;
        if (this.overrideGetMainHandHooks != null) {
            result = this.overrideGetMainHandHooks[this.overrideGetMainHandHooks.length - 1].getMainHand(livingEntity);
        } else {
            result = this.iPlayerModel.superGetMainHand(livingEntity);
        }

        if (this.afterGetMainHandHooks != null) {
            for (PlayerModelBase<T> afterGetMainHandHook : this.afterGetMainHandHooks) {
                afterGetMainHandHook.afterGetMainHand(livingEntity);
            }
        }

        return result;
    }

    protected PlayerModelBase<T> getOverwrittenGetMainHand(PlayerModelBase<T> overwriter)
    {
        if (this.overrideGetMainHandHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetMainHandHooks.length; i++) {
            if (this.overrideGetMainHandHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetMainHandHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetMainHandHookTypes = new LinkedList<>();
    private final static List<String> overrideGetMainHandHookTypes = new LinkedList<>();
    private final static List<String> afterGetMainHandHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeGetMainHandHooks;
    private PlayerModelBase<T>[] overrideGetMainHandHooks;
    private PlayerModelBase<T>[] afterGetMainHandHooks;
    public boolean isGetMainHandModded;
    private static final Map<String, String[]> allBaseBeforeGetMainHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetMainHandInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetMainHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetMainHandInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetMainHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetMainHandInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeGetRandomModelRenderer(CallbackInfoReturnable<ModelRenderer> callbackInfo, IPlayerModel<T> target, Random random)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isGetRandomModelRendererModded) {
            playerModelAPI.beforeGetRandomModelRenderer(callbackInfo, random);
        }
    }

    private void beforeGetRandomModelRenderer(CallbackInfoReturnable<ModelRenderer> callbackInfo, Random random)
    {
        if (this.beforeGetRandomModelRendererHooks != null) {
            for (int i = this.beforeGetRandomModelRendererHooks.length - 1; i >= 0; i--) {
                this.beforeGetRandomModelRendererHooks[i].beforeGetRandomModelRenderer(random);
            }
        }

        if (this.overrideGetRandomModelRendererHooks != null) {

            callbackInfo.setReturnValue(this.overrideGetRandomModelRendererHooks[this.overrideGetRandomModelRendererHooks.length - 1].getRandomModelRenderer(random));
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterGetRandomModelRenderer(IPlayerModel<T> target, Random random)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isGetRandomModelRendererModded) {
            playerModelAPI.afterGetRandomModelRenderer(random);
        }
    }

    private void afterGetRandomModelRenderer(Random random)
    {
        if (this.afterGetRandomModelRendererHooks != null) {
            for (PlayerModelBase<T> afterGetRandomModelBoxHook : this.afterGetRandomModelRendererHooks) {
                afterGetRandomModelBoxHook.afterGetRandomModelRenderer(random);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenGetRandomModelRenderer(PlayerModelBase<T> overwriter)
    {
        if (this.overrideGetRandomModelRendererHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetRandomModelRendererHooks.length; i++) {
            if (this.overrideGetRandomModelRendererHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetRandomModelRendererHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetRandomModelRendererHookTypes = new LinkedList<>();
    private final static List<String> overrideGetRandomModelRendererHookTypes = new LinkedList<>();
    private final static List<String> afterGetRandomModelRendererHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeGetRandomModelRendererHooks;
    private PlayerModelBase<T>[] overrideGetRandomModelRendererHooks;
    private PlayerModelBase<T>[] afterGetRandomModelRendererHooks;
    public boolean isGetRandomModelRendererModded;
    private static final Map<String, String[]> allBaseBeforeGetRandomModelRendererSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetRandomModelRendererInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRandomModelRendererSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRandomModelRendererInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRandomModelRendererSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRandomModelRendererInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void render(IPlayerModel<T> target, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isRenderModded) {
            playerModelAPI.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            target.superRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    private void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        if (this.beforeRenderHooks != null) {
            for (int i = this.beforeRenderHooks.length - 1; i >= 0; i--) {
                this.beforeRenderHooks[i].beforeRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        if (this.overrideRenderHooks != null) {
            this.overrideRenderHooks[this.overrideRenderHooks.length - 1].render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        } else {
            this.iPlayerModel.superRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        if (this.afterRenderHooks != null) {
            for (PlayerModelBase<T> afterRenderHook : this.afterRenderHooks) {
                afterRenderHook.afterRender(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenRender(PlayerModelBase<T> overwriter)
    {
        if (this.overrideRenderHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderHooks.length; i++) {
            if (this.overrideRenderHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderHookTypes = new LinkedList<>();
    private final static List<String> afterRenderHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeRenderHooks;
    private PlayerModelBase<T>[] overrideRenderHooks;
    private PlayerModelBase<T>[] afterRenderHooks;
    public boolean isRenderModded;
    private static final Map<String, String[]> allBaseBeforeRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeRenderCape(CallbackInfo callbackInfo, IPlayerModel<T> target, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isRenderCapeModded) {
            playerModelAPI.beforeRenderCape(callbackInfo, matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    private void beforeRenderCape(CallbackInfo callbackInfo, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        if (this.beforeRenderCapeHooks != null) {
            for (int i = this.beforeRenderCapeHooks.length - 1; i >= 0; i--) {
                this.beforeRenderCapeHooks[i].beforeRenderCape(matrixStack, buffer, packedLight, packedOverlay);
            }
        }

        if (this.overrideRenderCapeHooks != null) {
            this.overrideRenderCapeHooks[this.overrideRenderCapeHooks.length - 1].renderCape(matrixStack, buffer, packedLight, packedOverlay);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterRenderCape(IPlayerModel<T> target, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isRenderCapeModded) {
            playerModelAPI.afterRenderCape(matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    private void afterRenderCape(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        if (this.afterRenderCapeHooks != null) {
            for (PlayerModelBase<T> afterRenderCapeHook : this.afterRenderCapeHooks) {
                afterRenderCapeHook.afterRenderCape(matrixStack, buffer, packedLight, packedOverlay);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenRenderCape(PlayerModelBase<T> overwriter)
    {
        if (this.overrideRenderCapeHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderCapeHooks.length; i++) {
            if (this.overrideRenderCapeHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderCapeHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderCapeHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderCapeHookTypes = new LinkedList<>();
    private final static List<String> afterRenderCapeHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeRenderCapeHooks;
    private PlayerModelBase<T>[] overrideRenderCapeHooks;
    private PlayerModelBase<T>[] afterRenderCapeHooks;
    public boolean isRenderCapeModded;
    private static final Map<String, String[]> allBaseBeforeRenderCapeSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderCapeInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderCapeSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderCapeInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderCapeSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderCapeInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeRenderEars(CallbackInfo callbackInfo, IPlayerModel<T> target, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isRenderEarsModded) {
            playerModelAPI.beforeRenderEars(callbackInfo, matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    private void beforeRenderEars(CallbackInfo callbackInfo, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        if (this.beforeRenderEarsHooks != null) {
            for (int i = this.beforeRenderEarsHooks.length - 1; i >= 0; i--) {
                this.beforeRenderEarsHooks[i].beforeRenderEars(matrixStack, buffer, packedLight, packedOverlay);
            }
        }

        if (this.overrideRenderEarsHooks != null) {
            this.overrideRenderEarsHooks[this.overrideRenderEarsHooks.length - 1].renderEars(matrixStack, buffer, packedLight, packedOverlay);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterRenderEars(IPlayerModel<T> target, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isRenderEarsModded) {
            playerModelAPI.afterRenderEars(matrixStack, buffer, packedLight, packedOverlay);
        }
    }

    private void afterRenderEars(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay)
    {
        if (this.afterRenderEarsHooks != null) {
            for (PlayerModelBase<T> afterRenderEarsHook : this.afterRenderEarsHooks) {
                afterRenderEarsHook.afterRenderEars(matrixStack, buffer, packedLight, packedOverlay);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenRenderEars(PlayerModelBase<T> overwriter)
    {
        if (this.overrideRenderEarsHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderEarsHooks.length; i++) {
            if (this.overrideRenderEarsHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderEarsHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderEarsHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderEarsHookTypes = new LinkedList<>();
    private final static List<String> afterRenderEarsHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeRenderEarsHooks;
    private PlayerModelBase<T>[] overrideRenderEarsHooks;
    private PlayerModelBase<T>[] afterRenderEarsHooks;
    public boolean isRenderEarsModded;
    private static final Map<String, String[]> allBaseBeforeRenderEarsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderEarsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderEarsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderEarsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderEarsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderEarsInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void setLivingAnimations(IPlayerModel<T> target, T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetLivingAnimationsModded) {
            playerModelAPI.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        } else {
            target.superSetLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        }
    }

    private void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        if (this.beforeSetLivingAnimationsHooks != null) {
            for (int i = this.beforeSetLivingAnimationsHooks.length - 1; i >= 0; i--) {
                this.beforeSetLivingAnimationsHooks[i].beforeSetLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
            }
        }

        if (this.overrideSetLivingAnimationsHooks != null) {
            this.overrideSetLivingAnimationsHooks[this.overrideSetLivingAnimationsHooks.length - 1].setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        } else {
            this.iPlayerModel.superSetLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
        }

        if (this.afterSetLivingAnimationsHooks != null) {
            for (PlayerModelBase<T> afterSetLivingAnimationsHook : this.afterSetLivingAnimationsHooks) {
                afterSetLivingAnimationsHook.afterSetLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenSetLivingAnimations(PlayerModelBase<T> overwriter)
    {
        if (this.overrideSetLivingAnimationsHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideSetLivingAnimationsHooks.length; i++) {
            if (this.overrideSetLivingAnimationsHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideSetLivingAnimationsHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeSetLivingAnimationsHookTypes = new LinkedList<>();
    private final static List<String> overrideSetLivingAnimationsHookTypes = new LinkedList<>();
    private final static List<String> afterSetLivingAnimationsHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeSetLivingAnimationsHooks;
    private PlayerModelBase<T>[] overrideSetLivingAnimationsHooks;
    private PlayerModelBase<T>[] afterSetLivingAnimationsHooks;
    public boolean isSetLivingAnimationsModded;
    private static final Map<String, String[]> allBaseBeforeSetLivingAnimationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeSetLivingAnimationsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetLivingAnimationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetLivingAnimationsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetLivingAnimationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetLivingAnimationsInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void setModelAttributes(IPlayerModel<T> target, BipedModel<T> model)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isGetArmForSideModded) {
            playerModelAPI.setModelAttributes(model);
        } else {
            target.superSetModelAttributes(model);
        }
    }

    public void setModelAttributes(BipedModel<T> model)
    {
        if (this.beforeSetModelAttributesHooks != null) {
            for (int i = this.beforeSetModelAttributesHooks.length - 1; i >= 0; i--) {
                this.beforeSetModelAttributesHooks[i].beforeSetModelAttributes(model);
            }
        }

        if (this.overrideSetModelAttributesHooks != null) {
            this.overrideSetModelAttributesHooks[this.overrideSetModelAttributesHooks.length - 1].setModelAttributes(model);
        } else {
            this.iPlayerModel.superSetModelAttributes(model);
        }

        if (this.afterSetModelAttributesHooks != null) {
            for (PlayerModelBase<T> afterSetModelAttributesHook : this.afterSetModelAttributesHooks) {
                afterSetModelAttributesHook.afterSetModelAttributes(model);
            }
        }
    }

    protected PlayerModelBase<T> getOverwrittenSetModelAttributes(PlayerModelBase<T> overwriter)
    {
        if (this.overrideSetModelAttributesHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideSetModelAttributesHooks.length; i++) {
            if (this.overrideSetModelAttributesHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideSetModelAttributesHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeSetModelAttributesHookTypes = new LinkedList<>();
    private final static List<String> overrideSetModelAttributesHookTypes = new LinkedList<>();
    private final static List<String> afterSetModelAttributesHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeSetModelAttributesHooks;
    private PlayerModelBase<T>[] overrideSetModelAttributesHooks;
    private PlayerModelBase<T>[] afterSetModelAttributesHooks;
    public boolean isSetModelAttributesModded;
    private static final Map<String, String[]> allBaseBeforeSetModelAttributesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeSetModelAttributesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetModelAttributesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetModelAttributesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetModelAttributesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetModelAttributesInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeSetRotationAngles(CallbackInfo callbackInfo, IPlayerModel<T> target, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetRotationAnglesModded) {
            playerModelAPI.beforeSetRotationAngles(callbackInfo, entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
    }

    private void beforeSetRotationAngles(CallbackInfo callbackInfo, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        if (this.beforeSetRotationAnglesHooks != null) {
            for (int i = this.beforeSetRotationAnglesHooks.length - 1; i >= 0; i--) {
                this.beforeSetRotationAnglesHooks[i].beforeSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            }
        }

        if (this.overrideSetRotationAnglesHooks != null) {
            this.overrideSetRotationAnglesHooks[this.overrideSetRotationAnglesHooks.length - 1].setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterSetRotationAngles(IPlayerModel<T> target, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetRotationAnglesModded) {
            playerModelAPI.afterSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
    }

    private void afterSetRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        if (this.afterSetRotationAnglesHooks != null) {
            for (PlayerModelBase<T> afterSetRotationAnglesHook : this.afterSetRotationAnglesHooks) {
                afterSetRotationAnglesHook.afterSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            }
        }
    }

    public static <T extends LivingEntity> void setRotationAngles(IPlayerModel<T> target, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetRotationAnglesModded) {
            playerModelAPI.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        } else {
            target.superSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }
    }

    private void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch)
    {
        if (this.beforeSetRotationAnglesHooks != null) {
            for (int i = this.beforeSetRotationAnglesHooks.length - 1; i >= 0; i--) {
                this.beforeSetRotationAnglesHooks[i].beforeSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
            }
        }

        if (this.overrideSetRotationAnglesHooks != null) {
            this.overrideSetRotationAnglesHooks[this.overrideSetRotationAnglesHooks.length - 1].setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        } else {
            this.iPlayerModel.superSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
        }

        this.afterSetRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
    }

    protected PlayerModelBase<T> getOverwrittenSetRotationAngles(PlayerModelBase<T> overwriter)
    {
        if (this.overrideSetRotationAnglesHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideSetRotationAnglesHooks.length; i++) {
            if (this.overrideSetRotationAnglesHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideSetRotationAnglesHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeSetRotationAnglesHookTypes = new LinkedList<>();
    private final static List<String> overrideSetRotationAnglesHookTypes = new LinkedList<>();
    private final static List<String> afterSetRotationAnglesHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeSetRotationAnglesHooks;
    private PlayerModelBase<T>[] overrideSetRotationAnglesHooks;
    private PlayerModelBase<T>[] afterSetRotationAnglesHooks;
    public boolean isSetRotationAnglesModded;
    private static final Map<String, String[]> allBaseBeforeSetRotationAnglesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeSetRotationAnglesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetRotationAnglesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetRotationAnglesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetRotationAnglesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetRotationAnglesInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeSetVisible(CallbackInfo callbackInfo, IPlayerModel<T> target, boolean visible)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetVisibleModded) {
            playerModelAPI.beforeSetVisible(callbackInfo, visible);
        }
    }

    private void beforeSetVisible(CallbackInfo callbackInfo, boolean visible)
    {
        if (this.beforeSetVisibleHooks != null) {
            for (int i = this.beforeSetVisibleHooks.length - 1; i >= 0; i--) {
                this.beforeSetVisibleHooks[i].beforeSetVisible(visible);
            }
        }

        if (this.overrideSetVisibleHooks != null) {
            this.overrideSetVisibleHooks[this.overrideSetVisibleHooks.length - 1].setVisible(visible);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterSetVisible(IPlayerModel<T> target, boolean visible)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetVisibleModded) {
            playerModelAPI.afterSetVisible(visible);
        }
    }

    private void afterSetVisible(boolean visible)
    {
        if (this.afterSetVisibleHooks != null) {
            for (PlayerModelBase<T> afterSetVisibleHook : this.afterSetVisibleHooks) {
                afterSetVisibleHook.afterSetVisible(visible);
            }
        }
    }

    public static <T extends LivingEntity> void setVisible(IPlayerModel<T> target, boolean visible)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isSetVisibleModded) {
            playerModelAPI.setVisible(visible);
        } else {
            target.superSetVisible(visible);
        }
    }

    private void setVisible(boolean visible)
    {
        if (this.beforeSetVisibleHooks != null) {
            for (int i = this.beforeSetVisibleHooks.length - 1; i >= 0; i--) {
                this.beforeSetVisibleHooks[i].beforeSetVisible(visible);
            }
        }

        if (this.overrideSetVisibleHooks != null) {
            this.overrideSetVisibleHooks[this.overrideSetVisibleHooks.length - 1].setVisible(visible);
        } else {
            this.iPlayerModel.superSetVisible(visible);
        }

        this.afterSetVisible(visible);
    }

    protected PlayerModelBase<T> getOverwrittenSetVisible(PlayerModelBase<T> overwriter)
    {
        if (this.overrideSetVisibleHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideSetVisibleHooks.length; i++) {
            if (this.overrideSetVisibleHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideSetVisibleHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeSetVisibleHookTypes = new LinkedList<>();
    private final static List<String> overrideSetVisibleHookTypes = new LinkedList<>();
    private final static List<String> afterSetVisibleHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeSetVisibleHooks;
    private PlayerModelBase<T>[] overrideSetVisibleHooks;
    private PlayerModelBase<T>[] afterSetVisibleHooks;
    public boolean isSetVisibleModded;
    private static final Map<String, String[]> allBaseBeforeSetVisibleSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeSetVisibleInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetVisibleSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetVisibleInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetVisibleSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetVisibleInferiors = new Hashtable<>(0);

    // ############################################################################

    public static <T extends LivingEntity> void beforeTranslateHand(CallbackInfo callbackInfo, IPlayerModel<T> target, HandSide side, MatrixStack matrixStack)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isTranslateHandModded) {
            playerModelAPI.beforeTranslateHand(callbackInfo, side, matrixStack);
        }
    }

    private void beforeTranslateHand(CallbackInfo callbackInfo, HandSide side, MatrixStack matrixStack)
    {
        if (this.beforeTranslateHandHooks != null) {
            for (int i = this.beforeTranslateHandHooks.length - 1; i >= 0; i--) {
                this.beforeTranslateHandHooks[i].beforeTranslateHand(side, matrixStack);
            }
        }

        if (this.overrideTranslateHandHooks != null) {
            this.overrideTranslateHandHooks[this.overrideTranslateHandHooks.length - 1].translateHand(side, matrixStack);
            callbackInfo.cancel();
        }
    }

    public static <T extends LivingEntity> void afterTranslateHand(IPlayerModel<T> target, HandSide side, MatrixStack matrixStack)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isTranslateHandModded) {
            playerModelAPI.afterTranslateHand(side, matrixStack);
        }
    }

    private void afterTranslateHand(HandSide side, MatrixStack matrixStack)
    {
        if (this.afterTranslateHandHooks != null) {
            for (PlayerModelBase<T> afterTranslateHandHook : this.afterTranslateHandHooks) {
                afterTranslateHandHook.afterTranslateHand(side, matrixStack);
            }
        }
    }

    public static <T extends LivingEntity> void translateHand(IPlayerModel<T> target, HandSide side, MatrixStack matrixStack)
    {
        PlayerModelAPI<T> playerModelAPI = target.getPlayerModelAPI();
        if (playerModelAPI != null && playerModelAPI.isTranslateHandModded) {
            playerModelAPI.translateHand(side, matrixStack);
        } else {
            target.superTranslateHand(side, matrixStack);
        }
    }

    private void translateHand(HandSide side, MatrixStack matrixStack)
    {
        if (this.beforeTranslateHandHooks != null) {
            for (int i = this.beforeTranslateHandHooks.length - 1; i >= 0; i--) {
                this.beforeTranslateHandHooks[i].beforeTranslateHand(side, matrixStack);
            }
        }

        if (this.overrideTranslateHandHooks != null) {
            this.overrideTranslateHandHooks[this.overrideTranslateHandHooks.length - 1].translateHand(side, matrixStack);
        } else {
            this.iPlayerModel.superTranslateHand(side, matrixStack);
        }

        this.afterTranslateHand(side, matrixStack);
    }

    protected PlayerModelBase<T> getOverwrittenTranslateHand(PlayerModelBase<T> overwriter)
    {
        if (this.overrideTranslateHandHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideTranslateHandHooks.length; i++) {
            if (this.overrideTranslateHandHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideTranslateHandHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeTranslateHandHookTypes = new LinkedList<>();
    private final static List<String> overrideTranslateHandHookTypes = new LinkedList<>();
    private final static List<String> afterTranslateHandHookTypes = new LinkedList<>();
    private PlayerModelBase<T>[] beforeTranslateHandHooks;
    private PlayerModelBase<T>[] overrideTranslateHandHooks;
    private PlayerModelBase<T>[] afterTranslateHandHooks;
    public boolean isTranslateHandModded;
    private static final Map<String, String[]> allBaseBeforeTranslateHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeTranslateHandInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideTranslateHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideTranslateHandInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterTranslateHandSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterTranslateHandInferiors = new Hashtable<>(0);
}
