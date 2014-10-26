package modwarriors.notenoughkeys.asm;

import static modwarriors.notenoughkeys.asm.NEKCore.logger;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.*;

import net.minecraft.launchwrapper.IClassTransformer;

import modwarriors.notenoughkeys.asm.helper.ASMHelper;
import modwarriors.notenoughkeys.asm.helper.RemappingHelper;

/**
 * @author TheTemportalist
 */
public class ClassTransformer implements IClassTransformer {

	private static final String KEYBINDING_INTERNAL_NAME = RemappingHelper.getInternalClassName("net.minecraft.client.settings.KeyBinding");
	private static final String KEYBINDING_DESCRIPTOR = "L" + KEYBINDING_INTERNAL_NAME + ";";

	/**
	 * @param name            The full path name of the original class (can be obfuscated)
	 * @param transformedName The full path name of the new class (non obfuscated)
	 * @param basicClass      The bytes of the original .class
	 * @return The bytes of the new .class
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.client.settings.KeyBinding")) {
			logger.info("Found KeyBinding class");
			// check for obf
			boolean isObfedEnvironment = !name.equals(transformedName);
			// Move to manipulable object
			ClassNode classNode = ASMHelper.readClassFromBytes(basicClass);

			for (FieldNode field : classNode.fields) {
				if (field.name.equals("hash") && field.desc.equals(RemappingHelper.getDescriptor("net.minecraft.util.IntHashMap"))) {
					logger.info("Found hash field");
					field.desc = "Ljava/util/List;";
					field.name = "hash";
					logger.info("Field \"hash\" type changed successfully!");
				}
			}
			InsnList nodesToInject = new InsnList();
			for (MethodNode method : classNode.methods) {
				if (method.name.equals("onTick") && method.desc.equals("(I)V")) {
					logger.info("Found onTick method");

					AbstractInsnNode instruction = ASMHelper.getOrFindInstructionOfType(method.instructions.getFirst(), GETSTATIC, 1, false);

					// -> ILOAD
					instruction = instruction.getNext();
					// Remove GETSTATIC
					method.instructions.remove(instruction.getPrevious());
					// -> INVOKEVIRTUAL
					instruction = instruction.getNext();
					// Change INVOKEVIRTUAL
					((MethodInsnNode) instruction).setOpcode(INVOKESTATIC);
					((MethodInsnNode) instruction).owner = KEYBINDING_INTERNAL_NAME;
					((MethodInsnNode) instruction).name = "getKeyBindingsWithKey";
					((MethodInsnNode) instruction).desc = "(I)[" + KEYBINDING_DESCRIPTOR;
					// -> CHECKCAST
					instruction = instruction.getNext();
					// -> ASTORE
					instruction = instruction.getNext();
					// Remove CHECKCAST
					method.instructions.remove(instruction.getPrevious());
					// -> L3
					instruction = instruction.getNext();
					LabelNode L3 = (LabelNode) instruction;
					// -> LINENUMBER (We acknowledge their existence. That's all we do with these)
					instruction = instruction.getNext();
					// -> ALOAD
					instruction = instruction.getNext();
					// -> IFNULL
					instruction = instruction.getNext();
					// -> L4
					instruction = instruction.getNext();
					LabelNode L4 = (LabelNode) instruction;
					// Remove IFNULL
					method.instructions.remove(instruction.getPrevious());
					// Inject List of nodes
					// ARRAYLENGTH
					nodesToInject.add(new InsnNode(ARRAYLENGTH));
					// ISTORE
					nodesToInject.add(new VarInsnNode(ISTORE, 2));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// Remove next 8
					for (int i = 0; i < 7; i++) {
						method.instructions.remove(instruction.getNext());
					}
					// -> L1
					instruction = instruction.getNext();
					LabelNode L1 = (LabelNode) instruction;
					// Inject many things
					nodesToInject.add(new InsnNode(ICONST_0));
					nodesToInject.add(new VarInsnNode(ISTORE, 3));
					LabelNode L5 = new LabelNode();
					nodesToInject.add(L5);
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new VarInsnNode(ILOAD, 2));
					nodesToInject.add(new JumpInsnNode(IF_ICMPGE, (LabelNode) instruction));
					nodesToInject.add(new VarInsnNode(ALOAD, 1));
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new InsnNode(AALOAD));
					nodesToInject.add(new VarInsnNode(ASTORE, 4));
					LabelNode L6 = new LabelNode();
					nodesToInject.add(L6);
					nodesToInject.add(new VarInsnNode(ALOAD, 4));
					LabelNode L7 = new LabelNode();
					nodesToInject.add(new JumpInsnNode(IFNULL, L7));
					nodesToInject.add(new VarInsnNode(ALOAD, 4));
					nodesToInject.add(new InsnNode(DUP));
					nodesToInject.add(new FieldInsnNode(
							GETFIELD, KEYBINDING_INTERNAL_NAME,
							"pressTime", "I"
					));
					nodesToInject.add(new InsnNode(ICONST_1));
					nodesToInject.add(new InsnNode(IADD));
					nodesToInject.add(new FieldInsnNode(
							PUTFIELD, KEYBINDING_INTERNAL_NAME, "pressTime", "I"
					));
					nodesToInject.add(L7);
					nodesToInject.add(new IincInsnNode(3, 1));
					nodesToInject.add(new JumpInsnNode(GOTO, L5));
					// Inserts before L1
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// We are @ L1
					// -> L5
					instruction = instruction.getNext();

					LocalVariableNode localVar = method.localVariables.get(0);
					localVar.start = L6;
					localVar.end = L7;
					localVar.index = 4;
					method.localVariables.add(new LocalVariableNode("arr$",
							"[" + KEYBINDING_DESCRIPTOR, null, L3, L1, 1));
					method.localVariables.add(new LocalVariableNode("len$", "I", null, L4, L1, 2));
					method.localVariables.add(new LocalVariableNode("i$", "I", null, L5, L1, 3));

					// DONE w/ func
					logger.info("onTick method patched!");
				}
				else if (method.name.equals("setKeyBindState") && method.desc.equals("(IZ)V")) {
					logger.info("Found setKeyBindState method");
					AbstractInsnNode instruction = ASMHelper.getOrFindInstructionOfType(method.instructions.getFirst(), GETSTATIC, 1, false);

					// L2
					// -> ILOAD
					instruction = instruction.getNext();
					// -> Remove GETSTATIC
					method.instructions.remove(instruction.getPrevious());
					// -> InvokeVirtual
					instruction = instruction.getNext();
					((MethodInsnNode) instruction).setOpcode(INVOKESTATIC);
					((MethodInsnNode) instruction).owner = KEYBINDING_INTERNAL_NAME;
					((MethodInsnNode) instruction).name = "getKeyBindingsWithKey";
					((MethodInsnNode) instruction).desc = "(I)[" + KEYBINDING_DESCRIPTOR;
					// -> CheckCast
					instruction = instruction.getNext();
					// -> ASTORE
					instruction = instruction.getNext();
					// Remove CheckCast
					method.instructions.remove(instruction.getPrevious());
					// -> L3
					instruction = instruction.getNext();
					LabelNode L3 = (LabelNode) instruction;
					// -> LINENUMBER
					instruction = instruction.getNext();
					// -> ALoad
					instruction = instruction.getNext();
					// -> IFNULL
					instruction = instruction.getNext();
					// -> L4
					instruction = instruction.getNext();
					LabelNode L4 = (LabelNode) instruction;
					// Remove IFNULL
					method.instructions.remove(instruction.getPrevious());
					// Add nodes
					nodesToInject.add(new InsnNode(ARRAYLENGTH));
					nodesToInject.add(new VarInsnNode(ISTORE, 3));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					for (int i = 0; i < 4; i++) {
						method.instructions.remove(instruction.getNext());
					}
					// -> L1
					instruction = instruction.getNext();
					LabelNode L1 = (LabelNode) instruction;
					// Add Some Nodes
					nodesToInject.add(new InsnNode(ICONST_0));
					nodesToInject.add(new VarInsnNode(ISTORE, 4));
					// L5
					LabelNode L5 = new LabelNode();
					nodesToInject.add(L5);
					nodesToInject.add(new VarInsnNode(ILOAD, 4));
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new JumpInsnNode(IF_ICMPGE, L1));
					nodesToInject.add(new VarInsnNode(ALOAD, 2));
					nodesToInject.add(new VarInsnNode(ILOAD, 4));
					nodesToInject.add(new InsnNode(AALOAD));
					nodesToInject.add(new VarInsnNode(ASTORE, 5));
					// L6
					LabelNode L6 = new LabelNode();
					nodesToInject.add(L6);
					nodesToInject.add(new VarInsnNode(ALOAD, 5));
					LabelNode L7 = new LabelNode();
					nodesToInject.add(new JumpInsnNode(IFNULL, L7));
					// L8
					nodesToInject.add(new VarInsnNode(ALOAD, 5));
					nodesToInject.add(new VarInsnNode(ILOAD, 1));
					nodesToInject.add(new FieldInsnNode(
							PUTFIELD, KEYBINDING_INTERNAL_NAME,
							"pressed", "Z"
					));
					// L7
					nodesToInject.add(L7);
					nodesToInject.add(new IincInsnNode(4, 1));
					nodesToInject.add(new JumpInsnNode(GOTO, L5));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// -> Return
					instruction = instruction.getNext();
					// -> L5 (local vars)
					instruction = instruction.getNext();

					LocalVariableNode localVar = method.localVariables.get(0);
					localVar.start = L6;
					localVar.end = L7;
					localVar.index = 5;
					method.localVariables.add(new LocalVariableNode("arr$",
							"[" + KEYBINDING_DESCRIPTOR, null, L3, L1, 2));
					method.localVariables.add(new LocalVariableNode("len$", "I", null, L4, L1, 3));
					method.localVariables.add(new LocalVariableNode("i$", "I", null, L5, L1, 3));

					logger.info("setKeyBindState method patched!");

				}
				else if (method.name.equals("resetKeyBindingArrayAndHash") && method.desc.equals("()V")) {
					logger.info("Found resetKeyBindingArrayAndHash method");
					AbstractInsnNode instruction = ASMHelper.getOrFindInstructionOfType(method.instructions.getFirst(), GETSTATIC, 1, false);

					// At GetStatic
					((FieldInsnNode) instruction).desc = "Ljava/util/ArrayList;";
					// -> InvokeVirtual
					instruction = instruction.getNext();
					// At InvokeVirtual
					((MethodInsnNode) instruction).owner = "java/util/ArrayList";
					((MethodInsnNode) instruction).name = "clear";
					// Jump to L5 GetStatic
					instruction = ASMHelper.getOrFindInstructionOfType(instruction, GETSTATIC, 2, false);
					// At GetStatic
					((FieldInsnNode) instruction).desc = "Ljava/util/ArrayList;";
					// Remove next two
					instruction = instruction.getNext().getNext().getNext();
					method.instructions.remove(instruction.getPrevious().getPrevious());
					method.instructions.remove(instruction.getPrevious());
					// -> Invoke Virtual
					instruction = instruction.getNext();
					// At Invoke Virtual
					((MethodInsnNode) instruction).owner = "java/util/ArrayList";
					((MethodInsnNode) instruction).name = "add";
					((MethodInsnNode) instruction).desc = "(Ljava/lang/Object;)Z";
					// -> L6
					instruction = instruction.getNext();
					// Insert POP
					method.instructions.insertBefore(instruction, new InsnNode(POP));

					logger.info("resetKeyBindingArrayAndHash method patched!");
				}
				else if (method.name.equals("<init>") && method.desc.equals("(Ljava/lang/String;ILjava/lang/String;)V")) {
					logger.info("Found KeyBinding constructor");
					// -> L6 GetStatic
					AbstractInsnNode instruction = ASMHelper.getOrFindInstructionOfType(method.instructions.getFirst(), GETSTATIC, 2, false);

					((FieldInsnNode) instruction).owner = KEYBINDING_INTERNAL_NAME;
					((FieldInsnNode) instruction).desc = "Ljava/util/ArrayList;";
					// -> ALoad 0
					instruction = instruction.getNext().getNext();
					// Remove ILoad 2
					method.instructions.remove(instruction.getPrevious());
					// -> InvokeVirtual
					instruction = instruction.getNext();
					// modify
					((MethodInsnNode) instruction).owner = "java/util/ArrayList";
					((MethodInsnNode) instruction).name = "add";
					((MethodInsnNode) instruction).desc = "(Ljava/lang/Object;)Z";
					// -> L7
					instruction = instruction.getNext();
					// Insert pop
					method.instructions.insertBefore(instruction, new InsnNode(POP));

					logger.info("Constructor patched!");
				}

			}

			// getKeyBindingsWithKey method
			MethodNode method = new MethodNode(ACC_PRIVATE | ACC_STATIC,
					"getKeyBindingsWithKey", "(I)[" + KEYBINDING_DESCRIPTOR, null,
					null);
			logger.info("Creating new method getKeyBindingsWithKey");
			// L0
			LabelNode L0 = new LabelNode();
			method.instructions.add(L0);
			method.instructions.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
			method.instructions.add(new InsnNode(DUP));
			method.instructions.add(new MethodInsnNode(
					INVOKESPECIAL, "java/util/ArrayList",
					"<init>", "()V", false
			));
			method.instructions.add(new VarInsnNode(ASTORE, 1));
			// L1
			LabelNode L1 = new LabelNode();
			method.instructions.add(L1);
			method.instructions.add(new FieldInsnNode(
					GETSTATIC, KEYBINDING_INTERNAL_NAME,
					"hash", "Ljava.util/ArrayList;"
			));
			method.instructions.add(new MethodInsnNode(
					INVOKEVIRTUAL, "java/util/ArrayList",
					"iterator", "()Ljava/util/Iterator;", false
			));
			method.instructions.add(new VarInsnNode(ASTORE, 2));
			// L2
			LabelNode L2 = new LabelNode();
			method.instructions.add(L2);
			method.instructions.add(new VarInsnNode(ALOAD, 2));
			method.instructions.add(new MethodInsnNode(
					INVOKEINTERFACE, "java/util/Iterator",
					"hasNext", "()Z", true
			));
			LabelNode L3 = new LabelNode();
			method.instructions.add(new JumpInsnNode(IFEQ, L3));
			method.instructions.add(new VarInsnNode(ALOAD, 2));
			method.instructions.add(new MethodInsnNode(
							INVOKEINTERFACE, "java/util/Iterator",
							"next", "()Ljava/lang/Object;", true)
			);
			method.instructions.add(new TypeInsnNode(
					CHECKCAST, KEYBINDING_INTERNAL_NAME
			));
			method.instructions.add(new VarInsnNode(ASTORE, 3));
			// L4
			LabelNode L4 = new LabelNode();
			method.instructions.add(L4);
			method.instructions.add(new VarInsnNode(ALOAD, 3));
			method.instructions.add(new MethodInsnNode(
					INVOKEVIRTUAL, KEYBINDING_INTERNAL_NAME,
					"getKeyCode", "()I", false
			));
			method.instructions.add(new VarInsnNode(ILOAD, 0));
			LabelNode L5 = new LabelNode();
			method.instructions.add(new JumpInsnNode(IF_ICMPNE, L5));
			// L6
			method.instructions.add(new VarInsnNode(ALOAD, 1));
			method.instructions.add(new VarInsnNode(ALOAD, 3));
			method.instructions.add(new MethodInsnNode(
					INVOKEVIRTUAL, "java/util/ArrayList",
					"add", "(Ljava/lang/Object;)Z", false
			));
			method.instructions.add(new InsnNode(POP));
			// L5
			method.instructions.add(L5);
			method.instructions.add(new JumpInsnNode(GOTO, L2));
			// L3
			method.instructions.add(L3);
			method.instructions.add(new VarInsnNode(ALOAD, 1));
			method.instructions.add(new InsnNode(ICONST_0));
			method.instructions.add(new TypeInsnNode(
					ANEWARRAY, KEYBINDING_INTERNAL_NAME
			));
			method.instructions.add(new MethodInsnNode(
					INVOKEVIRTUAL, "java/util/ArrayList",
					"toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", false
			));
			method.instructions.add(new TypeInsnNode(
					CHECKCAST, "[" + KEYBINDING_DESCRIPTOR
			));
			method.instructions.add(new InsnNode(ARETURN));
			// L7
			LabelNode L7 = new LabelNode();
			method.instructions.add(L7);
			/*
			LOCALVARIABLE keyBinding Lmodwarriors/notenoughkeys/KeyBinding2; L4 L5 3
			LOCALVARIABLE i$ Ljava/util/Iterator; L2 L3 2
			LOCALVARIABLE keyCode I L0 L7 0
			LOCALVARIABLE validKeys Ljava/util/ArrayList; L1 L7 1
			// signature Ljava/util/ArrayList<Lmodwarriors/notenoughkeys/KeyBinding2;>;
			// declaration: java.util.ArrayList<modwarriors.notenoughkeys.KeyBinding2>
			MAXSTACK = 2
			MAXLOCALS = 4
			 */
			method.localVariables.add(new LocalVariableNode("keyBinding",
					KEYBINDING_DESCRIPTOR, null, L4, L5, 3));
			method.localVariables.add(new LocalVariableNode("i$",
					"Ljava/util/Iterator;", null, L2, L3, 2));
			method.localVariables.add(new LocalVariableNode("keyCode",
					"I", null, L0, L7, 0));
			method.localVariables.add(new LocalVariableNode("validKeys",
					"Ljava/util/ArrayList;", "Ljava/util/ArrayList<" + KEYBINDING_DESCRIPTOR + ">;", L1, L7, 1));

			// end method
			classNode.methods.add(method);

			logger.info("New method getKeyBindingsWithKey created!");
			logger.info("KeyBinding class transformed!");
			return ASMHelper.writeClassToBytes(classNode);
		}
		return basicClass;
	}

}
