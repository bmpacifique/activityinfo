# TODO: should support api as well, but we need to support pivot table requests through the API
@web @cross-browser
Feature: Indicator Targets
  As an analyst
  I want to be able to define target indicator values
  So I can measure actual results against our plans
  
  Background: 
    Given I have created a database "RRMP"
      And I have added partner "CRS" to "RRMP"
      And I have added partner "NRC" to "RRMP"
      And I have created a form named "NFI Distribution" in "RRMP"
      And I have created a quantity field "nb. kits" in "NFI Distribution"
      And I have created a quantity field "Satisfaction score" with aggregation "Average"
      And I have submitted a "NFI Distribution" form with:
        | field              | value  |
        | partner            | CRS    |
        | nb. kits           | 1000   |
        | Satisfaction score | 5.4    | 
      And I have submitted a "NFI Distribution" form with:
        | field              | value  |
        | partner            | NRC    |
        | nb. kits           | 800    |
      
  Scenario: Target by database
    When I create a target with values:
      | field       | value |
      | nb. kits    | 3000  |
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                     | Value |
      | Realized            | 1,800 |
      | Targeted            | 3,000 |

  Scenario: Defining multiple targets
    Given I have created a target named "UNICEF Goals" with values:
      | field              | value |
      | nb. kits           | 3000  |
      | Satisfaction score | 8.0   |
    And I have created a target named "Cluster Goals" with values:
      | field              | value |
      | nb. kits           | 2000  |
      | Satisfaction score | 10.0  |
    Then aggregating the indicators nb. kits and Satisfaction score by Indicator and Realized / Targeted should yield:
      |                    | Value |
      | nb. kits           |       |
      |   Realized         | 1,800 |
      |   Targeted         | 5,000 |
      | Satisfaction score |       |
      |   Realized         | 5.4   |
      |   Targeted         | 9     |

  @AI-1126
  Scenario: Defining database-level target for two activities
    And I have created a form named "Helmet form" in "RRMP"
    And I have created a quantity field "helmet" in "Helmet form"
    And I create a target with values:
      | field     | value |
      | nb. kits  | 3000  |
      | helmet    | 3     |

  Scenario: Defining targets by partner
    When I create a target for partner NRC with values:
      | field       | value |
      | nb. kits    | 1000  |
    And I create a target for partner CRS with values:
      | field       | value |
      | nb. kits    | 1500  |
    Then aggregating the indicator "nb. kits" by Partner and Realized / Targeted should yield:
      |             | Value |
      | CRS         |       |
      |   Realized  | 1,000 |
      |   Targeted  | 1,500 |
      | NRC         |       |
      |   Realized  |   800 |
      |   Targeted  | 1,000 |

  @AI-911
  Scenario: Defining target values with extra text
    When I create a target with values:
      | field       | value       |
      | nb. kits    | 4000  kits  |
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                     | Value |
      | Realized            | 1,800 |
      | Targeted            | 4,000 |

  @AI-911
  Scenario: Defining fractional target values
    When I create a target with values:
      | field                 | value       |
      | Satisfaction score    | 7.5         |
    Then aggregating the indicator "Satisfaction score" by Realized / Targeted should yield:
      |                       | Value       |
      | Realized              | 5.4         |
      | Targeted              | 7.5         |
    
  Scenario: No targets defined
    Given I haven not defined any targets
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                       | Value       |
      | Realized              | 1,800       |


  @AI-1066
  Scenario: Clearing target values
    When I create a target named "Goals"
    And I set the targets of "Goals" to:
      | field                 | value       |
      | nb. kits              | 4000  kits  |
      | Satisfaction score    | 7.5         |
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                       | Value       |
      | Realized              | 1,800       |
      | Targeted              | 4,000       |
    When I set the targets of "Goals" to:
      | field                 | value       |
      | nb. kits              | <blank>     |
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                       | Value       |
      | Realized              | 1,800       |
    When I set the targets of "Goals" to:
      | field                 | value       |
      | nb. kits              | 6000        |
    Then aggregating the indicator "nb. kits" by Realized / Targeted should yield:
      |                       | Value       |
      | Realized              | 1,800       |
      | Targeted              | 6,000       |

  @AI-1007
  Scenario: Test various UI scenarios based on database-level target
    When I create a target named "Target1"
    And I set the targets of "Target1" to:
      | field                 | value       |
      | nb. kits              | 4000  kits  |
      | Satisfaction score    | 7.5         |
    And I create a target named "Target2"
    And I set the targets of "Target2" to:
      | field                 | value       |
      | nb. kits              | 5000  kits  |
      | Satisfaction score    | 8.3         |
    Then selecting target "Target1" shows:
      | field                 | value       |
      | nb. kits              | 4000        |
      | Satisfaction score    | 7.5         |
    And selecting target "Target2" shows:
      | field                 | value       |
      | nb. kits              | 5000        |
      | Satisfaction score    | 8.3         |

  @AI-1096
  Scenario: Pivoting on deleted target
    When I create a target named "Goals"
    And I set the targets of "Goals" to:
      | field                 | value       |
      | nb. kits              | 4000  kits  |
    And I have removed the target "Goals"
    And I create a target named "Goals"
    And I set the targets of "Goals" to:
      | field                 | value       |
      | nb. kits              | 5000  kits  |
    Then aggregating the indicator "nb. kits" by Realized / Targeted and Year  should yield:
      |                       | 2014        |
      | Realized              | 1,800       |
      | Targeted              | 5,000       |
