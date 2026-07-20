import { RiskLevel } from './risk-level';

export type Coordinate = readonly [latitude: number, longitude: number];

export interface RiskFactor {
  readonly label: string;
  readonly value: string;
}

export interface RiskCell {
  readonly id: string;
  readonly name: string;
  readonly score: number;
  readonly level: RiskLevel;
  readonly summary: string;
  readonly factors: readonly RiskFactor[];
  readonly polygon: readonly Coordinate[];
}
