package edu.usc.infolab.tdvrp;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try {
			// LPTest1();
			// deliveryMIPTest();
			String dynamicDistFile = "data/dynamic_dist.txt";
			int TN = 25; // Time period number
			String distFile = "data/dist.txt";
			double[][][] staticTimeArray = DataRetrievalTest
					.readStaticDistArray(distFile, TN);
			double[][][] dynamicTimeArray = DataRetrievalTest
					.readDistArray(dynamicDistFile);

			// static road network
			System.out.println("Delivery using static route network...");
			deliveryMIPTest(staticTimeArray, dynamicTimeArray);

			// Dynamic road network
			System.out.println("Delivery using Dynamic route network...");
			deliveryMIPTest(dynamicTimeArray, dynamicTimeArray);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deliveryMIPTest(double[][][] T,
			double[][][] dynamicTimeArray) throws IloException, IOException {
		int TN = dynamicTimeArray[0][0].length; // Time period number
		int N = 7; // node number
		double f = 15; // time period length
		double P = 50; // penalty
		double B = 1e10;
		double[] S = new double[N]; // service time
		double[] w = new double[N]; // weight associated with customer
		double[] L = new double[N], U = new double[N]; // lower and upper bound
														// of delivery time
		double U2 = TN * f;
		double maxUpperBound = 120;
		for (int i = 0; i < N; ++i) {
			S[i] = 5;
			w[i] = 0.3;
			L[i] = 0;
			U[i] = maxUpperBound;
		}
		// variables
		IloCplex cplex = new IloCplex();
		cplex.setOut(new FileOutputStream("data/log.txt"));
		IloIntVar[] X = cplex.intVarArray(N * N * TN, 0, 1);
		IloIntVar[] Yl = cplex.intVarArray(N, 0, 1);
		IloIntVar[] Yu = cplex.intVarArray(N, 0, 1);
		IloNumVar[] T2 = cplex.numVarArray(N, 0, U2); // maximum leave time
		// objective
		IloLinearNumExpr target = cplex.linearNumExpr();
		// add \sum_{i,j,t}{X_{ij} * T_{ijt}}
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				for (int t = 0; t < TN; ++t) {
					target.addTerm(T[i][j][t], X[(i * N + j) * TN + t]);
				}
			}
		}
		// \sum_i^n{Yui + Yli} * P
		for (int i = 0; i < N; ++i) {
			target.addTerm(P, Yl[i]);
			target.addTerm(P, Yu[i]);
		}
		// \sum_i^n{w * T_i}
		for (int i = 0; i < N; ++i) {
			target.addTerm(w[i], T2[i]);
		}
		// add w[0] * Time when arrived at node 0
		for (int j = 1; j < N; ++j) {
			for (int t = 0; t < TN; ++t) {
				// target.addTerm(w[0], X[(j * N + 0) * TN + t]);
			}
		}
		cplex.addMinimize(target);

		// constraints
		// (2) T_0 = 0
		cplex.addEq(cplex.prod(1.0, T2[0]), 0, "(2)");
		// (3) \sum_{t,j} X_{ijt} = 1, (4) sum_{t,j} X_{ijt} = sum_{t,j} X_{jit}
		for (int i = 0; i < N; ++i) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			IloLinearNumExpr exp2 = cplex.linearNumExpr();
			for (int j = 0; j < N; ++j) {
				if (j == i)
					continue;
				for (int t = 0; t < TN; ++t) {
					exp.addTerm(1, X[(i * N + j) * TN + t]);
					exp2.addTerm(1, X[(j * N + i) * TN + t]);
				}
			}
			cplex.addEq(exp, 1, String.format("(3):(i=%d)", i));
			cplex.addEq(exp2, 1, String.format("(4):(i=%d)", i));

		}
		// (5) (6) (7)
		for (int t = 0; t < TN; ++t) {
			for (int i = 0; i < N; ++i) {
				for (int j = 1; j < N; ++j) {
					// if (i == j) continue;
					// (5) T_j - T_i - B X_{ijt} >= T_{ijt} + S_j - B
					IloLinearNumExpr exp = cplex.linearNumExpr();
					exp.addTerm(1, T2[j]);
					exp.addTerm(-1, T2[i]);
					exp.addTerm(-B, X[(i * N + j) * TN + t]);
					cplex.addGe(exp, T[i][j][t] + S[j] - B,
							String.format("(5):(%d, %d, %d)", i, j, t));
					// (6) T_i + B * X{ijt} <= f * (t + 1) + B
					exp = cplex.linearNumExpr();
					exp.addTerm(1, T2[i]);
					exp.addTerm(B, X[(i * N + j) * TN + t]);
					cplex.addLe(exp, f * (t + 1) + B,
							String.format("(6):(%d, %d, %d)", i, j, t));

					// (7) T_i >= f * t * X{ijt}
					exp = cplex.linearNumExpr();
					exp.addTerm(1, T2[i]);
					exp.addTerm(-f * t, X[(i * N + j) * TN + t]);
					cplex.addGe(exp, 0,
							String.format("(7):(%d, %d, %d)", i, j, t));
				}
			}

		}
		for (int i = 0; i < N; ++i) {
			// (8) T_i - B * Yu_i <= U_i, Upper bound constraint
			IloLinearNumExpr exp = cplex.linearNumExpr();
			exp.addTerm(1, T2[i]);
			exp.addTerm(-B, Yu[i]);
			cplex.addLe(exp, U[i], String.format("(8):(%d)", i));

			// (9) T_i + B * Yl_i >= L_i, Lower bound constraint
			exp = cplex.linearNumExpr();
			exp.addTerm(1, T2[i]);
			exp.addTerm(B, Yl[i]);
			cplex.addGe(exp, L[i], String.format("(9):(%d)", i));
		}
		if (cplex.solve()) {
			// Show and interpret the result
			int[] deliverySeq = new int[N];
			double[] departureTimes = new double[N];
			int[][] violation = new int[2][N];
			double objVal = cplex.getObjValue();
			cplex.output().println("Solution status = " + cplex.getStatus());
			// X[i,j,t]
			double[] val = cplex.getValues(X);
			int ncols = X.length;
			for (int col = 0; col < ncols; ++col)
				if (val[col] > 0) {
					int s = col / (N * TN);
					int e = (col - s * N * TN) / TN;
					int t = col % TN;
					deliverySeq[s] = e;
				}
			// T, departure times
			val = cplex.getValues(T2);
			for (int col = 0; col < T2.length; ++col) {
				departureTimes[col] = val[col];
			}

			val = cplex.getValues(Yl);
			for (int col = 0; col < Yl.length; ++col) {
				violation[0][col] = (int) val[col];
			}
			// bound violation
			val = cplex.getValues(Yu);
			for (int col = 0; col < Yu.length; ++col) {
				violation[1][col] = (int) val[col];
			}

			interpretResult(deliverySeq, departureTimes, violation, objVal,
					dynamicTimeArray);

			// calcuate total delivery time on dynamic road network
			// \sum_{i,j,t}{X_{ij} * T_{ijt}}
			double actualTime = 0;
			int start = 0;
			do {
				int next = deliverySeq[start];
				int t = (int) (actualTime / f);
				actualTime += dynamicTimeArray[start][next][t];
				if (next != 0) {
					actualTime += S[next]; // add service time
				}
				start = next;
				// System.out.println(actualTime);
			} while (start != 0);
			System.out.println(String.format("ActualTime: %s", actualTime));
		}
		cplex.end();
		// interpret result
	}

	private static void interpretResult(int[] deliverySeq,
			double[] departureTimes, int[][] violation, double objVal,
			double[][][] timeArray) {
		// TODO Auto-generated method stub
		// int N = deliverySeq.length;
		int cur = 0, N = deliverySeq.length;
		int f = 15;
		System.out.println("==========================================");
		System.out.println(String.format("Objective Val: %.3f", objVal));
		do {
			int t = (int) (departureTimes[cur] / f);
			int next = deliverySeq[cur];
			System.out.println(String.format("@%.3f: %d->%d",
					departureTimes[cur], cur, next));
			cur = next;
		} while (cur != 0);
		// time window violation summary
		System.out.println("Time window violation summary:");
		for (int i = 0; i < N; ++i) {
			if (violation[0][i] == 1) {
				System.out.println(String.format(
						"Lower bound of node %d is voilated.", i));
			}
			if (violation[1][i] == 1) {
				System.out.println(String.format(
						"Upper bound of node %d is voilated.", i));
			}
		}
	}

	private double getObjVal(int[] deliverySeq, double[][][] timeArray) {
		return 0;
	}
}
