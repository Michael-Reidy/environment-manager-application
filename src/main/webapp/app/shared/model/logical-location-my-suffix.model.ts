import { IEnvironmentMySuffix } from 'app/shared/model/environment-my-suffix.model';

export interface ILogicalLocationMySuffix {
  id?: number;
  name?: string | null;
  environment?: IEnvironmentMySuffix | null;
}

export const defaultValue: Readonly<ILogicalLocationMySuffix> = {};
