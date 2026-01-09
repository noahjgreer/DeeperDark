package net.minecraft.entity.ai.goal;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

public class GoalSelector {
   private static final PrioritizedGoal REPLACEABLE_GOAL = new PrioritizedGoal(Integer.MAX_VALUE, new Goal() {
      public boolean canStart() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map goalsByControl = new EnumMap(Goal.Control.class);
   private final Set goals = new ObjectLinkedOpenHashSet();
   private final EnumSet disabledControls = EnumSet.noneOf(Goal.Control.class);

   public void add(int priority, Goal goal) {
      this.goals.add(new PrioritizedGoal(priority, goal));
   }

   public void clear(Predicate predicate) {
      this.goals.removeIf((goal) -> {
         return predicate.test(goal.getGoal());
      });
   }

   public void remove(Goal goal) {
      Iterator var2 = this.goals.iterator();

      while(var2.hasNext()) {
         PrioritizedGoal prioritizedGoal = (PrioritizedGoal)var2.next();
         if (prioritizedGoal.getGoal() == goal && prioritizedGoal.isRunning()) {
            prioritizedGoal.stop();
         }
      }

      this.goals.removeIf((prioritizedGoalx) -> {
         return prioritizedGoalx.getGoal() == goal;
      });
   }

   private static boolean usesAny(PrioritizedGoal goal, EnumSet controls) {
      Iterator var2 = goal.getControls().iterator();

      Goal.Control control;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         control = (Goal.Control)var2.next();
      } while(!controls.contains(control));

      return true;
   }

   private static boolean canReplaceAll(PrioritizedGoal goal, Map goalsByControl) {
      Iterator var2 = goal.getControls().iterator();

      Goal.Control control;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         control = (Goal.Control)var2.next();
      } while(((PrioritizedGoal)goalsByControl.getOrDefault(control, REPLACEABLE_GOAL)).canBeReplacedBy(goal));

      return false;
   }

   public void tick() {
      Profiler profiler = Profilers.get();
      profiler.push("goalCleanup");
      Iterator var2 = this.goals.iterator();

      while(true) {
         PrioritizedGoal prioritizedGoal;
         do {
            do {
               if (!var2.hasNext()) {
                  this.goalsByControl.entrySet().removeIf((entry) -> {
                     return !((PrioritizedGoal)entry.getValue()).isRunning();
                  });
                  profiler.pop();
                  profiler.push("goalUpdate");
                  var2 = this.goals.iterator();

                  while(true) {
                     do {
                        do {
                           do {
                              do {
                                 if (!var2.hasNext()) {
                                    profiler.pop();
                                    this.tickGoals(true);
                                    return;
                                 }

                                 prioritizedGoal = (PrioritizedGoal)var2.next();
                              } while(prioritizedGoal.isRunning());
                           } while(usesAny(prioritizedGoal, this.disabledControls));
                        } while(!canReplaceAll(prioritizedGoal, this.goalsByControl));
                     } while(!prioritizedGoal.canStart());

                     Iterator var4 = prioritizedGoal.getControls().iterator();

                     while(var4.hasNext()) {
                        Goal.Control control = (Goal.Control)var4.next();
                        PrioritizedGoal prioritizedGoal2 = (PrioritizedGoal)this.goalsByControl.getOrDefault(control, REPLACEABLE_GOAL);
                        prioritizedGoal2.stop();
                        this.goalsByControl.put(control, prioritizedGoal);
                     }

                     prioritizedGoal.start();
                  }
               }

               prioritizedGoal = (PrioritizedGoal)var2.next();
            } while(!prioritizedGoal.isRunning());
         } while(!usesAny(prioritizedGoal, this.disabledControls) && prioritizedGoal.shouldContinue());

         prioritizedGoal.stop();
      }
   }

   public void tickGoals(boolean tickAll) {
      Profiler profiler = Profilers.get();
      profiler.push("goalTick");
      Iterator var3 = this.goals.iterator();

      while(true) {
         PrioritizedGoal prioritizedGoal;
         do {
            do {
               if (!var3.hasNext()) {
                  profiler.pop();
                  return;
               }

               prioritizedGoal = (PrioritizedGoal)var3.next();
            } while(!prioritizedGoal.isRunning());
         } while(!tickAll && !prioritizedGoal.shouldRunEveryTick());

         prioritizedGoal.tick();
      }
   }

   public Set getGoals() {
      return this.goals;
   }

   public void disableControl(Goal.Control control) {
      this.disabledControls.add(control);
   }

   public void enableControl(Goal.Control control) {
      this.disabledControls.remove(control);
   }

   public void setControlEnabled(Goal.Control control, boolean enabled) {
      if (enabled) {
         this.enableControl(control);
      } else {
         this.disableControl(control);
      }

   }
}
