import { INamespaceMySuffix } from 'app/shared/model/namespace-my-suffix.model';
import { ValueType } from 'app/shared/model/enumerations/value-type.model';
import { ExpressionType } from 'app/shared/model/enumerations/expression-type.model';

export interface ISettingMySuffix {
  id?: number;
  name?: string | null;
  valueType?: ValueType | null;
  expressionType?: ExpressionType | null;
  value?: string | null;
  namespace?: INamespaceMySuffix | null;
}

export const defaultValue: Readonly<ISettingMySuffix> = {};
