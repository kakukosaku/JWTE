# Java Grammar Related Concepts

1. Access Modifiers

| Class Relationship | private | default(can be omitted) | protect | public |
|--------------------|---------|-------------------------|---------|--------|
| Same Class         | yes     | yes                     | yes     | yes    |
| Same Package Subclass| no    | yes                     | yes     | yes    |
| Same Package Non-subclass| no| yes                     | yes     | yes    |
| Diff Package Subclass| no    | no                      | yes     | yes    |
| Diff Package Non-subclass| no| no                      | no      | yes    |

总结:

private 只能类内访问;

default +允许包内访问;

protect +包外子类访问;

public 都能访问.