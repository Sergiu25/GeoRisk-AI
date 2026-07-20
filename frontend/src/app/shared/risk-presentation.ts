import { RiskLevel } from '../core/models/risk-level';

export const RISK_COLORS: Readonly<Record<RiskLevel, string>> = {
  LOW: '#3f9b62',
  MODERATE: '#e5b93f',
  HIGH: '#d9574f',
};

export const RISK_RANGES: Readonly<Record<RiskLevel, string>> = {
  LOW: '0–39',
  MODERATE: '40–69',
  HIGH: '70–100',
};
