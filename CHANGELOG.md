# ReBalance 1.1.0
- Config is now rebalance.json5 (Make sure to move your config values over!)
- Added dynamic_reduction_start config value, defines when the dynamic reduction should kick in
- Added should_ignore_first_hit config value, defines if the first hit shouldn't be rebalanced, which is how it works in 1.0.0
- Fixed leap between damage numbers when minimum is met, for example with a minimum of 5.0, if dealing 4.0 damage the result would be 4.0, but at 5.0 it would be 2.5, being less than 4. Another example being weapons such as netherite swords deal less damage than stone swords when using a global reduction at 7 damage.