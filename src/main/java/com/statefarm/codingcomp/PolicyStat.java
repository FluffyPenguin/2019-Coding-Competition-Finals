package com.statefarm.codingcomp;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.statefarm.codingcomp.enums.PolicyStatus;
import com.statefarm.codingcomp.model.Policy;

public class PolicyStat {
    public static double findMeanPremium(List<Policy> policies, String policyType,
                                        PolicyStatus policyStatus, String state, int[] age, int[] accidents) {
      List<Policy> subsetPolicies = subsetAll(policies, policyType, policyStatus, state, age, accidents);
      double total= 0;
      int count = 0;

      for(Policy policy: subsetPolicies) {
          total += policy.getAnnualPremium();
          count++;
      }

      return total/count;
    }
    public static double findMedianPremium(List<Policy> policies, String policyType, PolicyStatus policyStatus,
                                          String state, int[] age, int[] accidents) {
        List<Policy> subsetPolicies = subsetAll(policies, policyType, policyStatus, state, age, accidents);
        List<Double> premiums = new ArrayList<Double>();

        for(Policy policy: subsetPolicies) {
            premiums.add(policy.getAnnualPremium());
        }
        Collections.sort(premiums);

        if(premiums.size() % 2 == 0) {
            return(premiums.get(premiums.size() / 2));
        } else {
          int topMiddle = (premiums.size() + 1) / 2;
          int botMiddle = (premiums.size() - 1) / 2;

          return (((premiums.get(topMiddle) + premiums.get(botMiddle)) / 2));
        }
    }

    public static double findStandardDeviation(List<Policy> policies, String policyType, PolicyStatus policyStatus,
                                              String state, int[] age, int[] accidents) {
        List<Policy> subsetPolicies = subsetAll(policies, policyType, policyStatus, state, age, accidents);
        List<Double> premiums = new ArrayList<Double>();

        for(Policy policy: subsetPolicies) {
            premiums.add(policy.getAnnualPremium());
        }

        return (calculateSD(premiums))  ;
    }

    public static int findN(List<Policy> policies, String policyType,
                                              PolicyStatus policyStatus, String state, int[] age, int[] accidents) {
        List<Policy> subsetPolicies = subsetAll(policies, policyType, policyStatus, state, age, accidents);

        return subsetPolicies.size();
    }
    public static List<Policy> subsetAll(List<Policy> policies, String policyType,
                                        PolicyStatus policyStatus, String state, int[] age, int[] accidents) {
        List<Policy> returnList = subsetPolicyType(policies, policyType);
        returnList = subsetPolicyStatus(returnList, policyStatus);
        returnList = subsetState(returnList, state);
        returnList = subsetAge(returnList, age);
        returnList = subsetAccidents(returnList, accidents);
        return (returnList);
    }
    public static List<Policy> subsetPolicyType(List<Policy> policies, String policyType) {
        if(policyType == null) {
            return (policies);
        } else {
          List<Policy> returnList = new ArrayList<Policy>();
          for (Policy policy: policies) {
              if (policy.getPolicyType().equals(policyType)) {
                  returnList.add(policy);
              }
          }
          return returnList;
        }
    }

    public static List<Policy> subsetPolicyStatus(List<Policy> policies, PolicyStatus policyStatus) {
        if(policyStatus == null) {
            return (policies);
        } else {
          List<Policy> returnList = new ArrayList<Policy>();
          for (Policy policy: policies) {
              if (policy.getPolicyStatus() == policyStatus) {
                  returnList.add(policy);
              }
          }
          return returnList;
        }
    }

    public static List<Policy> subsetState(List<Policy> policies, String state) {
        if(state == null) {
            return (policies);
        } else {
          List<Policy> returnList = new ArrayList<Policy>();
          for (Policy policy: policies) {
              if (policy.getState().equals(state)) {
                  returnList.add(policy);
              }
          }
          return returnList;
        }
    }

    public static List<Policy> subsetAge(List<Policy> policies, int[] age) {
        if(age.length == 0) {
            return (policies);
        } else {
          List<Policy> returnList = new ArrayList<Policy>();
          for (Policy policy: policies) {
              for(int a: age) {
                if (policy.getAge() == a) {
                    returnList.add(policy);
                }
              }
          }
          return returnList;
        }
    }

    public static List<Policy> subsetAccidents(List<Policy> policies, int[] accidents) {
        if(accidents.length == 0) {
            return (policies);
        } else {
          List<Policy> returnList = new ArrayList<Policy>();
          for (Policy policy: policies) {
              for(int a: accidents) {
                if (policy.getNumberOfAccidents() == a) {
                    returnList.add(policy);
                }
            }
          }
          return returnList;
        }
    }

    public static double calculateSD(List<Double> numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.size();

        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation/length);
    }
}
