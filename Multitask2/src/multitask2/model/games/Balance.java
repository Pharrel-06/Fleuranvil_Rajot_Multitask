package multitask2.model.games;

import com.github.forax.zen.KeyboardEvent.Key;

import multitask2.controller.Input;
import multitask2.model.Game;

/**
 * Mini-jeu d'équilibre : le joueur incline une planche et doit empêcher une
 * bille de sortir de ses limites.
 *
 * <p>
 * Les flèches gauche et droite règlent l'inclinaison. La gravité fait accélérer
 * la bille dans le sens de l'inclinaison, tandis qu'un facteur de friction
 * atténue sa vitesse.
 * </p>
 */
public final class Balance implements Game {

  /** Demi-longueur de la piste, en unités du modèle. */
  public static final double TRACK_HALF_LENGTH = 50;
  /** Valeur absolue maximale de l'inclinaison de la planche, en degrés. */
  public static final double MAX_TILT_DEGREES = 35;

  /** Accélération angulaire appliquée par le joueur, en degrés par seconde². */
  private static final double TILT_ACCEL_DEG = 260;
  /** Coefficient d'amortissement de la vitesse de rotation de la planche. */
  private static final double ANGULAR_DAMPING = 0.92;
  /** Accélération gravitationnelle de base utilisée par la simulation. */
  private static final double GRAVITY = 55;
  /** Coefficient de réduction de la vitesse appliqué à chaque mise à jour. */
  private static final double FRICTION = 0.995;
  /** Facteur de déséquilibre provoqué par la position de la bille sur la planche. */
  private static final double UNBALANCE_FACTOR = 1.6;
  /** Intensité de la dérive naturelle permanente de la planche. */
  private static final double DRIFT_STRENGTH = 18;

  /** Position longitudinale de la bille sur la piste, centrée en zéro. */
  private double position;
  /** Vitesse longitudinale de la bille. */
  private double speed;
  /** Inclinaison actuelle de la planche, en degrés. */
  private double tilt;
  /** Vitesse de rotation actuelle de la planche, en degrés par seconde. */
  private double angularSpeed;
  /** Sens actuel de la dérive naturelle de la planche ({@code -1} ou {@code 1}). */
  private double driftDirection;

  /**
   * Crée un jeu avec la bille au centre, immobile, et une planche horizontale.
   */
  public Balance() {
    position = 0;
    speed = 0;
    tilt = 0;
    angularSpeed = 0;
    driftDirection = 1;
    super();
  }

  /**
   * Met à jour les commandes d'inclinaison, la rotation de la planche, la
   * dynamique de la bille et vérifie les limites de la piste.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param input      état des entrées clavier
   * @param difficulty niveau de difficulté global
   * @return {@code true} tant que la bille reste dans les limites de la piste ;
   *         {@code false} si elle dépasse l'une de ces limites
   * @throws IllegalArgumentException si {@code dt} ou {@code difficulty} est
   *                                  négatif
   * @throws NullPointerException     si {@code input} est {@code null}
   */
  @Override
  public boolean update(double dt, Input input, double difficulty) {
    Game.checkInput(dt, input, difficulty);
    
    var dir = readInput(input);
    updatePlank(dt, dir, difficulty);
    positionAndSpeed(dt, difficulty);
    return checkBorder();
  }

  /**
   * Lit les touches gauche et droite maintenues pour déterminer la direction de
   * rotation demandée par le joueur, et met à jour le sens de la dérive.
   *
   * @param input état des entrées clavier
   * @return {@code -1} pour incliner à gauche, {@code 1} pour incliner à droite,
   *         ou {@code 0} si aucune (ou les deux) touche n'est maintenue
   */
  private int readInput(Input input) {
    var dir = (input.isKeyDown(Key.LEFT) ? -1 : 0) + (input.isKeyDown(Key.RIGHT) ? 1 : 0);
    if (dir != 0) {
      driftDirection = dir;
    }
    return dir;
  }
  
  /**
   * Met à jour la vitesse angulaire et l'inclinaison de la planche en combinant
   * l'action du joueur, le poids de la bille et la dérive permanente.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param dir        direction d'inclinaison demandée par le joueur
   * @param difficulty niveau de difficulté global
   */
  private void updatePlank(double dt, int dir, double difficulty) {
    var instability = (position * UNBALANCE_FACTOR) + (driftDirection * DRIFT_STRENGTH * (1 + difficulty / 6));
    angularSpeed += (dir * TILT_ACCEL_DEG + instability) * dt;
    angularSpeed *= ANGULAR_DAMPING;
    tilt = Game.dontExceed(tilt + angularSpeed * dt, MAX_TILT_DEGREES);
    resetAngularSpeedAtLimit();
  }
  
  /**
   * Annule la vitesse de rotation lorsque la planche atteint son inclinaison
   * maximale afin d'éviter un blocage contre la butée.
   */
  private void resetAngularSpeedAtLimit() {
    if (Math.abs(tilt) >= MAX_TILT_DEGREES) {
      angularSpeed = 0;
    }
  }

  /**
   * Calcule l'accélération liée à l'inclinaison, puis intègre la vitesse et la
   * position de la bille. La difficulté augmente l'accélération gravitationnelle
   * appliquée.
   *
   * @param dt         durée du pas de simulation, en secondes
   * @param difficulty niveau de difficulté global
   */
  private void positionAndSpeed(double dt, double difficulty) {
    var acceleration = GRAVITY * Math.sin(Math.toRadians(tilt)) * (1 + difficulty / 4);
    speed += acceleration * dt;
    speed *= FRICTION;
    position += speed * dt;
  }

  /**
   * Limite la position de la bille aux bords de la piste et détermine si elle les
   * a franchis.
   *
   * @return {@code true} si la position n'a pas été corrigée par la limitation,
   *         {@code false} si la bille a dépassé un bord
   */
  private boolean checkBorder() {
    var old = position;
    position = Game.dontExceed(position, TRACK_HALF_LENGTH);
    return old == position;
  }

  /**
   * Retourne la position longitudinale courante de la bille.
   *
   * @return position de la bille, comprise entre les limites de la piste après
   *         une mise à jour
   */
  public double position() {
    return position;
  }

  /**
   * Retourne l'inclinaison courante de la planche.
   *
   * @return inclinaison en degrés
   */
  public double tilt() {
    return tilt;
  }
}