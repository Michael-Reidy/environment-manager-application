import dayjs from 'dayjs';
import { INamespaceMySuffix } from 'app/shared/model/namespace-my-suffix.model';
import { ILogicalLocationMySuffix } from 'app/shared/model/logical-location-my-suffix.model';

export interface IEnvironmentMySuffix {
  id?: number;
  name?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  inheritsFrom?: IEnvironmentMySuffix | null;
  contains?: INamespaceMySuffix[] | null;
  appliesTos?: ILogicalLocationMySuffix[] | null;
}

export const defaultValue: Readonly<IEnvironmentMySuffix> = {};
